package Core;

import java.util.LinkedList;

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
 * This class is responsible for turning raw text into lists of tokens.
 *
 * @author Adam Scarr
 * @since r1
 */
public class Tokenizer {
    private LexiconDAO lexicon;

    /**
     * Class constructor.
     * @param lex The lexicon to pull words from.
     */
    public Tokenizer(LexiconDAO lex) {
        lexicon = lex;
    }

    /**
     * <p>
     * Used to convert between line of text and a list of tokens.
     * </p>
     *
     * <p>
     * If multiple tokens match a given word then multiple lists of tokens will be returned.
     * </p>
     *
     * @param line  The text to convert.
     * @return  all possible variations of token lists that match the text.
     */

    // todo: Need to implement methods here to deal with multiple tokens.
    public LinkedList<Sentance> tokenize(String line) {
		String[] words = line.split(" ");
		Sentance sentance = new Sentance();

		for (String word : words) {
			Token w = lexicon.get(word);

			if(w.getType().equals("UNDEF")) {
				// If we couldnt find the word perhaps it has a comma or period on the end?
				// Search this word for periods or commas (usually at the end of a word not by themselves.
				// todo: ownership on proper nouns ('s).
                char last = word.charAt(word.length() - 1);
                if(last == ',' || last == '.') {
                    w = lexicon.get(word.substring(0, word.length() - 1));
                } else {
                    w = lexicon.get(word);
                }

				if(w.getType().equals("UNDEF")) {
					 System.out.println("Could not find '" + w.getValue() + "' in lexicon.");
				}
				if(word.charAt(word.length() - 1) == '.') {
					sentance.add(lexicon.get(word.substring(0, word.length() - 1)));
					sentance.add(new Token(".", "PERIOD", true, true, true, true, true, true));
				} else if(word.charAt(word.length() - 1) == ',') {
					sentance.add(lexicon.get(word.substring(0, word.length() - 1)));
					sentance.add(new Token(",", "COMMA", true, true, true, true, true, true));
				}
			} else {
				sentance.add(w);
			}
		}

        LinkedList<Sentance> sentances = new LinkedList<Sentance>();
        sentances.add(sentance);
		return sentances;
	}
}
