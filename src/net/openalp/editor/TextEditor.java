package net.openalp.editor;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */

public class TextEditor extends JScrollPane {
    private JTextPane textPane;
    private StyledDocument doc;
    private Style badGrammar;
    private Style badSpelling;
    private Style normal;
    private ModificationObserver observer;

    public TextEditor() {
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(800,600));
        textPane = new JTextPane();
        doc = textPane.getStyledDocument();

        normal = doc.addStyle("Normal", null);
        StyleConstants.setForeground(normal, Color.DARK_GRAY);

        badGrammar = doc.addStyle("BadGrammar", null);
        StyleConstants.setForeground(badGrammar, Color.GREEN);

        badSpelling = doc.addStyle("BadSpelling", null);
        StyleConstants.setForeground(badSpelling, Color.RED);

        observer = new ModificationObserver(this);
        textPane.getDocument().addDocumentListener(observer);

        setViewportView(textPane);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    StyledDocument getDocument() {
        return doc;
    }

    public String getSentenceText(int start, int length) {
        try {
            return doc.getText(start, length);
        } catch (BadLocationException ex) {
            return null;
        }
    }

    public String getText() {
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            return null;
        }
    }

    public void markBadGrammar(final int start, int end) {
        final int length = end - start;
        System.out.println("Bad Grammar");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                doc.setCharacterAttributes(start, length, badGrammar, true);
            }
        });
    }

    public void markBadSpelling(final int start, int end) {
        final int length = end - start;
        System.out.println("Bad Speeling :)");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                doc.setCharacterAttributes(start, length, badSpelling, true);
            }
        });
    }

    void markNormal(final int start, int end) {
        final int length = end - start;
        System.out.println("Bad Grammar");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                doc.setCharacterAttributes(start, length, normal, true);
            }
        });
    }
}
