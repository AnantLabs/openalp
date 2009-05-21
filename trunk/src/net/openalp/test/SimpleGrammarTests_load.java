package net.openalp.test;

import net.openalp.core.Grammar;
import net.openalp.core.encoding.GrammarDecoder;
import net.openalp.graph.Graph;
import net.openalp.graph.GraphView;

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
 * MERCHANTABILtITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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
public class SimpleGrammarTests_load {
    public static void main(String[] args) {
        GrammarDecoder loader = new GrammarDecoder();
        Grammar test = (Grammar)loader.load("../testsave.oag");        
    }
}
