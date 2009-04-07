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
 * FileParser allows for text files to be parsed by Grammar.
 *
 * @author      Adam Scarr
 * @author      Rowan Spence
 * @since       r35
 * @see         Grammar
 **/

package net.openalp.core;
import java.io.*;

public class FileParser {
    Grammar grammar;

    public FileParser(Grammar grammar) {
        this.grammar = grammar;
    }

    // This is designed to run a file that will create a grammar AND test its validity to speed up
    // development times by detecting breakages early.
    // Reads a 'test' file: a file that contains a number of lines each representing an action to manipulate the grammar.
	// a: - adds a line
	// v: - checks that a line validates correctly
	// i: - checks that a line does not calculateSentanceValidity correctly.
	public boolean testFile(String filename) {
		boolean valid = true;
		try {
			BufferedReader trainingSet = new BufferedReader(new FileReader(new File(filename)));
			String line;

			while ((line = trainingSet.readLine()) != null) {
				switch(line.charAt(0)) {
					case 'a':
						grammar.parse(line.substring(2));
						break;

					case 'v':
						if(grammar.calculateSentanceValidity(line.substring(2)) == 0) {
							System.out.println("Error validating: " + line.substring(2));
							valid = false;
						}
						break;

					case 'i':
						if(grammar.calculateSentanceValidity(line.substring(2)) > 0) {
							System.out.println("Error invalidating: " + line.substring(2));
							valid = false;
						}
						break;

				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find test set '" + filename + "': " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return valid;
	}

    // Reads a text file and passes each line into parse, building the grammar.
    // Unlike testFile it will not do any tests, its really just a quick way to build a grammar.
	// Lines beginning with # are ignored.
	public void parseFile(String filename) {
		try {
			BufferedReader trainingSet = new BufferedReader(new FileReader(new File(filename)));
			String line;

			while ((line = trainingSet.readLine()) != null) {
				if(line.charAt(0) != '#') {
					grammar.parse(line);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find training set '" + filename + "': " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
