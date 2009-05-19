package net.openalp.editor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import net.openalp.core.FileParser;
import net.openalp.core.Grammar;
import net.openalp.core.LexiconDAO;
import net.openalp.core.Sentence;
import net.openalp.core.Tokenizer;

/**
 * Observes changes in the editor. When the user stops typing for a few seconds,
 * or the sentance is completed the sentance is tokenized and parsed.
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class ModificationObserver implements DocumentListener, Runnable {
    private TextEditor editor;
    private char[] sentanceDelimiters = {'.', '!', '?'};
    private static final long editDelay = 800;
    private Thread sleeper;
    private LexiconDAO lex;
    private Grammar grammar;
    private Tokenizer tokenizer;

    public ModificationObserver(TextEditor editor) {
        this.editor = editor;
        lex = new LexiconDAO();
        grammar = new Grammar(lex);
        tokenizer = new Tokenizer(lex);

        FileParser parser = new FileParser(grammar);
        parser.parseFile("data/conjunctions.txt");
        parser.parseFile("data/trainingset.txt");
    }



    private boolean isSentanceDelimiter(char c) {
        for(int i = 0; i < sentanceDelimiters.length; i++) {
            if(sentanceDelimiters[i] == c) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the beginning and end of the sentance containing the given char.
     * @param charPos
     */
    private SentencePosition findSentancePosition(int charPos) {
        int start = charPos;
        int end = charPos;
        boolean complete = false;


        try {
            while (start >= 0 && !isSentanceDelimiter(editor.getDocument().getText(start - 1, 1).charAt(0))) {
                start--;
            }
        } catch (BadLocationException ignore) {}

        try {
            while (!isSentanceDelimiter(editor.getDocument().getText(end + 1, 1).charAt(0))) {
                if(isSentanceDelimiter(editor.getDocument().getText(end + 1, 1).charAt(0))) {
                    complete = true;
                }
                end++;
            }
        } catch (BadLocationException ignore) {}

        SentencePosition p = new SentencePosition(start,end);
        p.complete = complete;

        return p;
    }

    public void insertUpdate(DocumentEvent e) {
        final SentencePosition sp = findSentancePosition(e.getOffset());

        if(sp.complete) {
            checkSentence(sp);
        } else {
            new Thread(new Runnable() {
                public void run() {
                    waitForDelay(sp);
                }
            }).start();
        }
    }

    public void removeUpdate(DocumentEvent e) {

    }

    public void changedUpdate(DocumentEvent e) {

    }

    public void waitForDelay(SentencePosition sp) {

        if(sleeper != null) sleeper.interrupt();
        
        synchronized(this) {
            sleeper = Thread.currentThread();
            try {
                // Sleep, and if we wake up uninterrupted we can check the sentence.
                Thread.sleep(editDelay);
                checkSentence(sp);
            } catch (InterruptedException ex) {
                // If we are woken up early then another change was made before
                // the timer expired.
                
            }
            sleeper = null;
        }
    }

    private synchronized void checkSentence(SentencePosition sp) {
        String text = editor.getSentenceText(sp);
        // Grammar first because its sentance level.
        if(grammar.calculateSentanceValidity(text) < 0.4f) {
            editor.markBadGrammar(sp);
        } else {
            editor.markNormal(sp);
        }


        // need to check spelling one at a time... Perhaps there is a better
        // way... perhaps as part of grammar...
        List<String> sentence = tokenizer.split(text);
        int curPos = sp.start;
        for(String word: sentence) {
            if(word.length() > 0 && lex.get(word.toLowerCase()).size() == 0) {
                editor.markBadSpelling(new SentencePosition(curPos, curPos + word.length()));
            }
            
            // TODO: Ugly and will lead to errors with some punc.
            // Only way around this will be to write a fully fledged tokenizing engine
            // that returns all tokens, including spaces etc...
            curPos += word.length() + 1;
        }
    }


    public void run() {

    }
}
