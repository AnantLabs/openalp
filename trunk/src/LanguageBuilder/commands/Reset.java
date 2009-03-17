package LanguageBuilder.commands;

import GenericComponents.CommandListener;
import GenericComponents.Console;
import Core.Grammar;

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
 * Removes all nodes and links from the grammar.
 *
 * @author Adam Scarr
 * @since r37
 */
public class Reset implements CommandListener {
    private Grammar grammar;

    public Reset(Grammar grammar) {
        this.grammar = grammar;
    }

    public String getCommand() {
        return "reset";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        grammar.clear();
    }
}
