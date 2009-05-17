package net.openalp.core;

import java.util.LinkedList;
import java.util.List;

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

    public  Tokenizer() { };
    /**
     * Class constructor.
     * @param lex The lexicon to pull words from.
     */
    public Tokenizer(LexiconDAO lex) {
        lexicon = lex;
    }

    public LexiconDAO getLexicon() {
        return lexicon;
    }

    public void setLexicon(LexiconDAO lexicon) {
        this.lexicon = lexicon;
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
    public LinkedList<Sentance> tokenize(String line) {
		List<String> words = split(line);
		Sentance sentance = new Sentance();

		for (String word : words) {
            LinkedList<Token> tokens = lexicon.get(word);

			if(tokens.size() == 0) {
				System.out.println("Could not find '" + word + "' in lexicon.");
                sentance.add(new Token("UNDEF"));
                continue;
			}

            sentance.add(tokens.get(0));
		}

        LinkedList<Sentance> sentances = new LinkedList<Sentance>();
        sentances.add(sentance);
		return sentances;
	}

    private List<String> split(String sentance) {
        LinkedList<String> chunks = new LinkedList<String>();
        int cursor = 0;
        int lastToken = 0;
        while(cursor < sentance.length()) {
            switch(sentance.charAt(cursor)) {
                // Delimiters first.
                case ' ':
                    if(cursor > lastToken) chunks.add(sentance.substring(lastToken, cursor));
                    lastToken = cursor + 1;
                    break;

                // Special characters that form tokens automatically.
                case '.':
                case ',':
                case '"':
                case '(':
                case ')':
                case '\'':
                    if(cursor > lastToken) chunks.add(sentance.substring(lastToken, cursor));
                    lastToken = cursor + 1;
                    chunks.add(Character.toString(sentance.charAt(cursor)));
                    break;
            }
            cursor++;
        }

        return chunks;
    }

}
