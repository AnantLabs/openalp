package LanguageBuilder.commands;

import GenericComponents.CommandListener;
import GenericComponents.Console;

/**
 *  This file is part of OpenALP.
 *
 *  OpenALP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  OpenALP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenALP.  If not, see <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>.
 *
 *  A command that will display a simple help message.
 *
 * @author      Adam Scarr
 * @since       r37
 **/
public class Help implements CommandListener {

    public String getCommand() {
        return "help";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        System.out.println("::help::");
        System.out.println("  add (sentance) - Create a new path in the grammar");
        System.out.println("  define (word) (type) (perspectiveMask) (tenseMask) - Create a word in the dictionary");
        System.out.println("  remove (word) - Removes a word from the dictionary");
        System.out.println("  search (word) - Finds a word in the dictionary");
        System.out.println("  test (filename) - Runs the given test file through the grammar");
        System.out.println("  train (filename) - Runs the given file into the parser building the grammar");
        System.out.println("  help - Displays this message");
    }
}
