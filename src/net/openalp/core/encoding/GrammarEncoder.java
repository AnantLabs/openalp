package net.openalp.core.encoding;

import net.openalp.core.Grammar;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedOutputStream;
import java.beans.XMLEncoder;


public class GrammarEncoder {
    private Grammar grammar;

    public GrammarEncoder(Grammar grammar) {
        this.grammar = grammar;
    }

    public GrammarEncoder(Grammar grammar, String filename) {
        this.grammar = grammar;
        save(filename);
    }

    public boolean save(String filename) {
        try {
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
            encoder.writeObject(grammar);
            encoder.close();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }
}
