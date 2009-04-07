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
 *  Bootstraps the LanguageBuilder.
 *
 * @author      Adam Scarr
 * @since       r37
 **/

package net.openalp.languagebuilder;

import net.openalp.core.FileParser;

public class StartLanguageBuilder {
    public static void main(String[] args) {
		LanguageBuilderFrame lb = new LanguageBuilderFrame();

        FileParser parser = new FileParser(lb.grammar);
        parser.parseFile("data/conjunctions.txt");
        parser.parseFile("data/trainingset.txt");
	}
}
