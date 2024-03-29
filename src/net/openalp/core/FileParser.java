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

/**
 * Used to parse some simple file formats.
 * @author Adam Scarr
 * @since r50
 */
public class FileParser {
    private Grammar grammar;

    /**
     * Sets up a new FileParser for grammar.
     * @param grammar The grammar to use.
     */
    public FileParser(Grammar grammar) {
        this.grammar = grammar;
    }

    /**
     * This is designed to run a file that will create grammar and tests its vailidity agains some predefined rules.
     *
     * The test file format is very simple, each line starts with a letter followed by a colon.
     * <ul>
     * <li><b>a:</b> adds a line</li>
     * <li><b>v:</b> validates a line</li>
     * <li><b>i:</b> invalidates a line</li>
     * </ul>
     * If any of of validate rules fail to vailidate or any of the invalidate rules are valid
     * it will display an error showing which line and sentance.
     * @param filename The file to parse
     * @return true if the file vaildated correctly otherwise false.
     */
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
						if(!grammar.calculateSentenceValidity(line.substring(2)).isValid()) {
							System.out.println("Error validating: " + line.substring(2));
							valid = false;
						}
						break;

					case 'i':
						if(grammar.calculateSentenceValidity(line.substring(2)).isValid()) {
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

    /**
     * Reans in a simple file with sentances seperated by newlines, and builds the grammar.
     *
     * A hash(#) at the beginning of a line means that line will not be parsed.
     * @param filename
     */
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
