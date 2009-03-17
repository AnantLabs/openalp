package LanguageBuilder.commands;

import GenericComponents.CommandListener;
import GenericComponents.Console;
import Core.FileParser;
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
 * Runs a test file, creating the grammar and validating it based on the rules in the test file.
 *
 * @author Adam Scarr
 * @since r37
 */
public class Test implements CommandListener {
    private Grammar grammar;

    public Test(Grammar grammar) {
        this.grammar = grammar;
    }

    public String getCommand() {
        return "test";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        if(argv[0].equalsIgnoreCase("test"))   {
            FileParser parser = new FileParser(grammar);
            System.out.println(parser.testFile("data/" + argv[1]));
        }
    }
}
