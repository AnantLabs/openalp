package net.openalp.core.encoding;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: NecromancyBlack
 * Date: 17/05/2009
 * Time: 8:52:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrammarDecoder {
    XMLDecoder decoder;

    public GrammarDecoder() {}

    public Object load(String filename)
    {
        try {
            decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return null;
        }

        return decoder.readObject();
    }
}
