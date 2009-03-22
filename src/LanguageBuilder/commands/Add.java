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
 * Creates new grammar based on the given sentance.
 *
 * @author Adam Scarr
 * @since r37
 */

package LanguageBuilder.commands;

import GenericComponents.CommandListener;
import GenericComponents.Console;
import Core.Grammar;

public class Add implements CommandListener {
    private Grammar grammar;

    public Add(Grammar grammar) {
        this.grammar = grammar;
    }


    public String getCommand() {
        return "add";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        StringBuilder sentance = new StringBuilder();

        for(String word: argv) {
            sentance.append(word);
            sentance.append(' ');
        }

        grammar.parse(sentance.toString());
    }
}
