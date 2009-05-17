package net.openalp.core.encoding;

import net.openalp.core.Grammar;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedOutputStream;
import java.beans.XMLEncoder;


public class GraphEncoder {

    public GraphEncoder(String filename, Grammar grammar) {
        try {
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
            encoder.writeObject(grammar);
            encoder.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        XMLEncoder encoder = new XMLEncoder(System.out);
//        encoder.writeObject(grammar);
//        encoder.close();
    }
}
