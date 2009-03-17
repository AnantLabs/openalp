package LanguageBuilder.commands;

import GenericComponents.CommandListener;
import GenericComponents.Console;
import Core.Token;
import Core.Lexicon;

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
 * Adds a new token to the lexicon.
 *
 * @author Adam Scarr
 * @since r37
 */
public class Define implements CommandListener {
    Lexicon lexicon;

    public Define(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

    public String getCommand() {
        return "define";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        int perspectiveMask = Integer.parseInt(argv[3]);
			int tenseMask = Integer.parseInt(argv[4]);

			lexicon.add(new Token(argv[1], argv[2], (perspectiveMask & 4) == 4, (perspectiveMask & 2) == 2,(perspectiveMask & 1) == 1,
					                                (tenseMask & 4)       == 4, (tenseMask & 2)       == 2, (tenseMask & 1)      == 1));
    }
}
