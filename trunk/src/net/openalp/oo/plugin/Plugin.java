/* LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package net.openalp.oo.plugin;

/** OpenOffice 3.x Integration
 *
 * @author Marcin Miłkowski
 */
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.linguistic2.XSpellAlternatives;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.Locale;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XServiceDisplayName;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.linguistic2.*;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.task.XJobExecutor;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import net.openalp.core.FileParser;
import net.openalp.core.Grammar;
import net.openalp.core.LexiconDAO;
import net.openalp.languagebuilder.LanguageBuilderFrame;


public class Plugin extends WeakBase implements XJobExecutor, XServiceDisplayName, XServiceInfo, XProofreader, XLinguServiceEventBroadcaster, XSpellChecker {
    private List<XLinguServiceEventListener> xEventListeners;
    private Grammar grammar;
    private LexiconDAO lexicon;
    private static final String[] SERVICE_NAMES = {"com.sun.star.linguistic2.Proofreader", 
                                                   "com.sun.star.linguistic2.SpellChecker",
                                                   "net.openalp.oo.plugin.Plugin"};
    private XComponentContext xContext;
    private Locale[] locales = {new Locale("en", "GB", ""),
        new Locale("en", "US", ""),
        new Locale("en", "AU", ""),
        new Locale("en", "CA", "")};

    public Plugin(final XComponentContext xCompContext) {
        try {
            //LanguageBuilderFrame lb = new LanguageBuilderFrame();
            //FileParser parser = new FileParser(lb.getGrammar());
            //grammar = lb.getGrammar();
            lexicon = new LexiconDAO();
            grammar = new Grammar(lexicon);
            FileParser parser = new FileParser(grammar);
            parser.parseFile("data/conjunctions.txt");
            parser.parseFile("data/trainingset.txt");

            changeContext(xCompContext);
            xEventListeners = new ArrayList<XLinguServiceEventListener>();
        } catch (final Throwable t) {
            showError(t);
        }
    }

    public void changeContext(final XComponentContext xCompContext) {
        xContext = xCompContext;
    }

    private XComponent getxComponent() {
        try {
            final XMultiComponentFactory xMCF = xContext.getServiceManager();
            final Object desktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", xContext);
            final XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(
                    XDesktop.class, desktop);
            final XComponent xComponent = xDesktop.getCurrentComponent();
            return xComponent;
        } catch (final Throwable t) {
            showError(t);
            return null;
        }
    }

    /**
     * Runs the grammar checker on paragraph text.
     *
     *
     */
    public final ProofreadingResult doProofreading(final String docID,
            final String paraText, final Locale locale, final int startOfSentencePos,
            final int nSuggestedBehindEndOfSentencePosition, PropertyValue[] props) {
        
        final ProofreadingResult paRes = new ProofreadingResult();
        System.out.println("[Start Proofreading]");
        System.out.println(paraText);
        System.out.println("startOfSentancePos = " + startOfSentencePos);
        System.out.println("endOfSentancePos = " + nSuggestedBehindEndOfSentencePosition);

        try {
            paRes.nStartOfSentencePosition = startOfSentencePos;
            paRes.nBehindEndOfSentencePosition = nSuggestedBehindEndOfSentencePosition;
            paRes.xProofreader = this;
            paRes.aLocale = locale;
            paRes.aDocumentIdentifier = docID;
            paRes.aText = paraText;
            paRes.aProperties = props;

            String sentance = paraText.substring(startOfSentencePos, nSuggestedBehindEndOfSentencePosition);

            if(sentance.length() > 0 && grammar.calculateSentanceValidity(sentance) > 0.0) {
                System.out.println("Sentance validates. No error.");
                paRes.aErrors = new SingleProofreadingError[0];
            } else {
                System.out.println("Validation failed. Error here.");
                paRes.aErrors = new SingleProofreadingError[1];
                paRes.aErrors[0] = createOOoError(locales[0], startOfSentencePos, nSuggestedBehindEndOfSentencePosition);
            }
            System.out.println(paRes);
            System.out.println("[END PROOFREADING]");
            System.out.println("");
            return paRes;

        } catch (final Throwable t) {
            showError(t);
            return paRes;
        }
    }

    /**
     * Creates a SingleGrammarError object for use in OOo.
     *
     *
     */
    private SingleProofreadingError createOOoError(final Locale locale, int start, int end) {

        final SingleProofreadingError aError = new SingleProofreadingError();
        aError.nErrorType = com.sun.star.text.TextMarkupType.PROOFREADING;
        aError.aFullComment = "Invalid Grammar";
        aError.aShortComment = "Invalid Grammar";
        aError.aSuggestions = new String[0];
        aError.nErrorStart = start;
        aError.nErrorLength = end;
        aError.aRuleIdentifier = "NO_PATH";
        aError.aProperties = new PropertyValue[0];
        return aError;
    }

    /**
     * LT does not support spell-checking, so we return false.
     *
     * @return true
     */
    public final boolean isSpellChecker() {
        return true;
    }

    /**
     * Runs LT options dialog box.
     **/
    public final void runOptionsDialog() {
        // Not implemented. who cares.
    }

    /**
     * @return An array of Locales supported by LT.
     */
    public final Locale[] getLocales() {
        return locales;
    }

    /**
     * @return true if LT supports the language of a given locale.
     * @param locale
     *          The Locale to check.
     */
    public final boolean hasLocale(final Locale locale) {
        for (Locale l : locales) {
            if (l.Language.equals(locale.Language)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a listener that allow re-checking the document after changing the
     * options in the configuration dialog box.
     *
     * @param xLinEvLis
     *          - the listener to be added
     * @return true if listener is non-null and has been added, false otherwise.
     */
    public final boolean addLinguServiceEventListener(
            final XLinguServiceEventListener xLinEvLis) {
        if (xLinEvLis == null) {
            return false;
        }
        xEventListeners.add(xLinEvLis);
        return true;
    }

    /**
     * Remove a listener from the event listeners list.
     *
     * @param xLinEvLis
     *          - the listener to be removed
     * @return true if listener is non-null and has been removed, false otherwise.
     */
    public final boolean removeLinguServiceEventListener(
            final XLinguServiceEventListener xLinEvLis) {
        if (xLinEvLis == null) {
            return false;
        }
        if (xEventListeners.contains(xLinEvLis)) {
            xEventListeners.remove(xLinEvLis);
            return true;
        }
        return false;
    }

    /**
     * Inform listener (grammar checking iterator) that options have changed and
     * the doc should be rechecked.
     *
     */
    public final void resetDocument() {
        if (!xEventListeners.isEmpty()) {
            for (final XLinguServiceEventListener xEvLis : xEventListeners) {
                if (xEvLis != null) {
                    final com.sun.star.linguistic2.LinguServiceEvent xEvent = new com.sun.star.linguistic2.LinguServiceEvent();
                    xEvent.nEvent = com.sun.star.linguistic2.LinguServiceEventFlags.PROOFREAD_AGAIN;
                    xEvLis.processLinguServiceEvent(xEvent);
                }
            }
        }
    }

    public String[] getSupportedServiceNames() {
        return getServiceNames();
    }

    public static String[] getServiceNames() {
        return SERVICE_NAMES;
    }

    public boolean supportsService(final String sServiceName) {
        for (final String sName : SERVICE_NAMES) {
            if (sServiceName.equals(sName)) {
                return true;
            }
        }
        return false;
    }

    public String getImplementationName() {
        return Plugin.class.getName();
    }

    public static XSingleComponentFactory __getComponentFactory(
            final String sImplName) {
        SingletonFactory xFactory = null;
        if (sImplName.equals(Plugin.class.getName())) {
            xFactory = new SingletonFactory();
        }
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(final XRegistryKey regKey) {
        return Factory.writeRegistryServiceInfo(Plugin.class.getName(), Plugin.getServiceNames(), regKey);
    }

    public void trigger(final String sEvent) {
        if (!javaVersionOkay()) {
            return;
        }
        try {
            if (sEvent.equals("configure")) {
                runOptionsDialog();
            } else if (sEvent.equals("about")) {
            } else {
                System.err.println("Sorry, don't know what to do, sEvent = " + sEvent);
            }
        } catch (final Throwable e) {
            showError(e);
        }
    }

    private boolean javaVersionOkay() {
        final String version = System.getProperty("java.version");
        if (version != null && (version.startsWith("1.0") || version.startsWith("1.1") || version.startsWith("1.2") || version.startsWith("1.3") || version.startsWith("1.4"))) {
            final DialogThread dt = new DialogThread(
                    "Error: LanguageTool requires Java 1.5 or later. Current version: " + version);
            dt.start();
            return false;
        }
        if ("1.6.0_10".equals(version)) { //no newer version has it
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
                // Well, what can we do...
            }
        }
        return true;
    }

    static void showError(final Throwable e) {
        String msg = "An error has occured in LanguageTool:\n" + e.toString() + "\nStacktrace:\n";
        final StackTraceElement[] elem = e.getStackTrace();
        for (final StackTraceElement element : elem) {
            msg += element.toString() + "\n";
        }
        final DialogThread dt = new DialogThread(msg);
        dt.start();
    // e.printStackTrace();
    // OOo crashes when we throw an Exception :-(
    // throw new RuntimeException(e);
    }

    public boolean isValid(String word, Locale locale, PropertyValue[] properties) {
        return lexicon.get(word) != null;
    }

    public XSpellAlternatives spell(String word, Locale locale, PropertyValue[] arg2) {
        return new SpellingAlternatives(word, locale, SpellFailure.SPELLING_ERROR);
    }

    private class SpellingAlternatives implements XSpellAlternatives {
        private String word;
        private Locale locale;
        private short failure;

        public SpellingAlternatives(String word, Locale locale, short failure) {
            this.word = word;
            this.locale = locale;
            this.failure = failure;
        }

        public String getWord() {
            return word;
        }

        public Locale getLocale() {
            return locale;
        }

        public short getFailureType() {
            return failure;
        }

        public short getAlternativesCount() {
            return 0;
        }

        public String[] getAlternatives() {
            return new String[0];
        }

    }

    private class AboutDialogThread extends Thread {

        private ResourceBundle messages;

        AboutDialogThread(final ResourceBundle messages) {
            this.messages = messages;
        }

        public void run() {
            final XModel model = (XModel) UnoRuntime.queryInterface(XModel.class,
                    getxComponent());
            final XWindow parentWindow = model.getCurrentController().getFrame().getContainerWindow();
            final XWindowPeer parentWindowPeer = (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, parentWindow);
        }
    }

    public void ignoreRule(String ruleId, Locale locale) {
    }

    /**
     * Called on rechecking the document - resets the ignore status for rules that
     * was set in the spelling dialog box or in the context menu.
     *
     * The rules disabled in the config dialog box are left as intact.
     */
    public void resetIgnoreRules() {
    }

    public String getServiceDisplayName(Locale locale) {
        return "OpenALP Proofreader";
    }
}
/**
 * A simple comparator for sorting errors by their position.
 *
 */
class ErrorPositionComparator implements Comparator<SingleProofreadingError> {

    public int compare(SingleProofreadingError match1,
            SingleProofreadingError match2) {
        int error1pos = match1.nErrorStart;
        int error2pos = match2.nErrorStart;
        if (error1pos > error2pos) {
            return 1;
        } else if (error1pos < error2pos) {
            return -1;
        } else {
            return ((Integer) (match1.aSuggestions.length)).compareTo(match2.aSuggestions.length);
        }
    }
}

class DialogThread extends Thread {

    private String text;

    DialogThread(final String text) {
        this.text = text;
    }

    public void run() {
        JOptionPane.showMessageDialog(null, text);
    }
}