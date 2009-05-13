package net.openalp.editor;

import java.awt.event.ActionEvent;
import net.openalp.core.Grammar;
import net.openalp.core.LexiconDAO;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class EditorFrame extends JFrame {
    private LexiconDAO lexicon = new LexiconDAO();
    private Grammar grammar = new Grammar(lexicon);

    public EditorFrame() {
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_X);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        file.add(exit);
        menu.add(file);
        add(menu, BorderLayout.NORTH);
        add(new TextEditor(), BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


}
