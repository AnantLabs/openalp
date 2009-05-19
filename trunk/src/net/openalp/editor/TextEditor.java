package net.openalp.editor;

import java.awt.Color;
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

public class TextEditor extends JScrollPane  {
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

    public String getSentenceText(SentencePosition sp) {
        try {
            return doc.getText(sp.start, sp.getLength());
        } catch (BadLocationException ex) {
            return "";
        }
    }

    public void markBadGrammar(final SentencePosition sp) {
        System.out.println("Bad Grammar");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                doc.setCharacterAttributes(sp.start, sp.getLength(), badGrammar, true);
            }
        });
    }

    public void markBadSpelling(final SentencePosition sp) {
        System.out.println("Bad Speeling :)");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                doc.setCharacterAttributes(sp.start, sp.getLength(), badSpelling, true);
            }
        });
    }

    void markNormal(final SentencePosition sp) {
        System.out.println("Bad Grammar");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                doc.setCharacterAttributes(sp.start, sp.getLength(), normal, true);
            }
        });
    }
}
