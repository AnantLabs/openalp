package net.openalp.editor;

import javax.swing.*;
import java.awt.Dimension;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */

public class TextEditor extends JScrollPane {
    public TextEditor() {
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(800,600));

        JTextArea textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        
        setViewportView(textArea);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    
}
