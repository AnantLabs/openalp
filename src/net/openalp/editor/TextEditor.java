package net.openalp.editor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */

public class TextEditor extends JScrollPane implements DocumentListener {
    JTextArea textArea;
    private char[] sentanceDelimiters = {'.', '!', '?'};

    public TextEditor() {
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(800,600));

        textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.getDocument().addDocumentListener(this);
        
        setViewportView(textArea);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    public boolean isSentanceDelimiter(char c) {
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
    public SentancePosition findSentancePosition(int charPos) {
        int start = charPos;
        int end = charPos;

        try {
            while (start >= 0 && !isSentanceDelimiter(textArea.getDocument().getText(start - 1, 1).charAt(0))) {
                start--;
            }
        } catch (BadLocationException ignore) {}

        try {
            while (!isSentanceDelimiter(textArea.getDocument().getText(end, 1).charAt(0))) {
                end++;
            }
        } catch (BadLocationException ignore) {}



        return new SentancePosition(start,end);
    }

    public void insertUpdate(DocumentEvent e) {
        SentancePosition sp = findSentancePosition(e.getOffset());
        try {
            System.out.println(textArea.getDocument().getText(sp.start, sp.getLength()));
        } catch (BadLocationException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeUpdate(DocumentEvent e) {
      
    }

    public void changedUpdate(DocumentEvent e) {
        
    }

    private class SentancePosition {
        int start, end;

        public SentancePosition(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getLength() {
            return end - start;
        }

    }
}
