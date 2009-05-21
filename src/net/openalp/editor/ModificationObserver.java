package net.openalp.editor;

import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.openalp.core.FileParser;
import net.openalp.core.Grammar;
import net.openalp.core.LexiconDAO;
import net.openalp.core.ParseResult;
import net.openalp.core.Tokenizer;
import net.openalp.core.TokenizingError;
import net.openalp.languagebuilder.LanguageBuilderFrame;

/**
 * Observes changes in the editor. When the user stops typing for a few seconds,
 * or the sentance is completed the sentance is tokenized and parsed.
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class ModificationObserver implements DocumentListener, Runnable {
    private TextEditor editor;
    private static final long editDelay = 800;
    private Thread sleeper;
    private Grammar grammar;

    public ModificationObserver(TextEditor editor) {
        this.editor = editor;

        LanguageBuilderFrame lb = new LanguageBuilderFrame();

        grammar = lb.getGrammar();

        FileParser parser = new FileParser(grammar);
        parser.parseFile("data/conjunctions.txt");
        parser.parseFile("data/trainingset.txt");

    }

    public void insertUpdate(DocumentEvent e) {
        new Thread(new Runnable() {
            public void run() {
                waitForDelay();
            }
        }).start();
    }

    public void removeUpdate(DocumentEvent e) {

    }

    public void changedUpdate(DocumentEvent e) {

    }

    public void waitForDelay() {

        if(sleeper != null) sleeper.interrupt();
        
        synchronized(this) {
            sleeper = Thread.currentThread();
            try {
                // Sleep, and if we wake up uninterrupted we can check the sentence.
                Thread.sleep(editDelay);
                checkSentences();
            } catch (InterruptedException ex) {
                // If we are woken up early then another change was made before
                // the timer expired.
                
            }
            sleeper = null;
        }
    }

    private synchronized void checkSentences() {
        List<ParseResult> result = grammar.validate(editor.getText());

        for(ParseResult sentenceResult: result) {

            if(sentenceResult.isValid()) {
                editor.markNormal(sentenceResult.getStart(), sentenceResult.getEnd());
            } else {
                editor.markBadGrammar(sentenceResult.getStart(), sentenceResult.getEnd());
            }

            for(TokenizingError error: sentenceResult.getTokenizingResult().getErrors()) {
                editor.markBadSpelling(error.getStart(), error.getEnd());
            }
        }
    }


    public void run() {

    }
}
