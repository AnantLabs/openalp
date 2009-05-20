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
    public TokenizingResult tokenize(String line) {
		return tokenize(split(line));
	}

    public TokenizingResult tokenize(List<Word> words) {
        Sentence sentance = new Sentence();

        TokenizingResult result = new TokenizingResult();

		for (Word word : words) {
            LinkedList<Token> tokens = lexicon.get(word.getText());

			if(tokens.size() == 0) {
                result.addError(new TokenizingError(word.getStart(), word.getEnd()));
				System.out.println("Could not find '" + word + "' in lexicon.");
                sentance.add(new Token("UNDEF"));
                continue;
			}

            sentance.add(tokens.get(0));
		}

        LinkedList<Sentence> sentances = new LinkedList<Sentence>();
        sentances.add(sentance);
        result.setSentences(sentances);
		return result;
    }

    public List<Word> split(String sentance) {
        LinkedList<Word> words = new LinkedList<Word>();
        int cursor = 0;
        int lastToken = 0;
        while(cursor < sentance.length()) {
            switch(sentance.charAt(cursor)) {
                // Delimiters first.
                case ' ':
                    if(cursor > lastToken) words.add(new Word(sentance.substring(lastToken, cursor), lastToken, cursor));
                    
                    lastToken = cursor + 1;
                    break;

                // Special characters that form tokens automatically.
                case '.':
                case ',':
                case '"':
                case '(':
                case ')':
                case '\'':
                    if(cursor > lastToken) words.add(new Word(sentance.substring(lastToken, cursor), lastToken, cursor));
                    lastToken = cursor + 1;
                    words.add(new Word(Character.toString(sentance.charAt(cursor)), cursor, cursor + 1));
                    break;
            }
            cursor++;
        }

        /*
         * For debugging...
        for(Word word: words) {
            System.out.println("::" + word.getText() + "::");
            System.out.println(" Start: " + word.getStart());
            System.out.println(" End: " + word.getEnd());
        }
         */

        return words;
    }

}
