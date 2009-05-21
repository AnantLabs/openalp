package net.openalp.languagebuilder.commands;

import net.openalp.generic.swing.CommandListener;
import net.openalp.generic.swing.Console;
import net.openalp.core.Grammar;

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
 * Tests a sentance against the grammar and returns its validity. 
 *
 * @author Adam Scarr
 * @since r37
 */
public class Check implements CommandListener {
    private Grammar grammar;

    public Check(Grammar grammar) {
        this.grammar = grammar;
    }

    public String getCommand() {
        return "check";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        StringBuilder sentance = new StringBuilder();

        for(String word: argv) {
            sentance.append(word);
            sentance.append(' ');
        }

        System.out.print("Checking '" + sentance + "': ");

        System.out.println(grammar.calculateSentenceValidity(sentance.toString()));
    }
}
