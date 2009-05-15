package net.openalp.editor;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */

public class TextEditor extends JScrollPane implements DocumentListener {
    private JTextPane textPane;
    private StyledDocument doc;
    private char[] sentanceDelimiters = {'.', '!', '?'};
    private Style badGrammar;
    private Style badSpelling;

    public TextEditor() {
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(800,600));
        textPane = new JTextPane();
        doc = textPane.getStyledDocument();

        badGrammar = doc.addStyle("BadGrammar", null);
        StyleConstants.setForeground(badGrammar, Color.GREEN);

        badSpelling = doc.addStyle("BadSpelling", null);
        StyleConstants.setBackground(badSpelling, Color.RED);

        textPane.getDocument().addDocumentListener(this);


        setViewportView(textPane);
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
            while (start >= 0 && !isSentanceDelimiter(doc.getText(start - 1, 1).charAt(0))) {
                start--;
            }
        } catch (BadLocationException ignore) {}

        try {
            while (!isSentanceDelimiter(doc.getText(end + 1, 1).charAt(0))) {
                end++;
            }
        } catch (BadLocationException ignore) {}



        return new SentancePosition(start,end);
    }

    public void insertUpdate(DocumentEvent e) {
        final SentancePosition sp = findSentancePosition(e.getOffset());
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    doc.setCharacterAttributes(sp.start, sp.getLength(), badGrammar, true);

                    String sentance = doc.getText(sp.start, sp.getLength());
                    System.out.println(sentance);
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        

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
