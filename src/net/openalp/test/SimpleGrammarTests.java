package net.openalp.test;

import net.openalp.core.LexiconDAO;
import net.openalp.core.Grammar;
import net.openalp.core.FileParser;
import net.openalp.core.encoding.GraphEncoder;
import net.openalp.core.encoding.GraphDecoder;

/**
 * This file is part of OpenALP.
 * <p/>
 * OpenALP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * OpenALP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with OpenALP.  If not, see <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>.
 * <p/>
 *
 * Runs through a few simple tests to validate the core logic in grammar.
 *
 * @author Adam Scarr
 * @since r1
 */
public class SimpleGrammarTests {
    public static void main(String[] args) {
        LexiconDAO lexicon = new LexiconDAO();
        Grammar grammar = new Grammar(lexicon);
        FileParser fp = new FileParser(grammar);

        fp.testFile("data/simple.tst");
        grammar.clear();
        fp.testFile("data/reverse.tst");
        new GraphEncoder("../testsave.oag", grammar);
        GraphDecoder loader = new GraphDecoder("../testsave.oag");
        Object test = loader.getGrammar();
    }
}
