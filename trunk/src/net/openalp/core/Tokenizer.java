package net.openalp.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
    private char[] terminators = {'.', '!', '?'};
    private char[] symbols = {',', '\'', '"', '(', ')'};
    private char[] whitespace = {' ', '\n', '\r', '\t'};
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
     * @param text  The text to convert.
     * @return  A Tokenizing result for each sentence in the text.
     */
    public List<TokenizedSentence> tokenize(String text) {
		return tokenize(split(text));
	}

    public List<TokenizedSentence> tokenize(List<UntokenizedSentence> sentences) {
        List<TokenizedSentence> result = new Vector<TokenizedSentence>();

        for (UntokenizedSentence untokenizedSentence: sentences) {
            System.out.println("New Sentence...");
            result.add(tokenize(untokenizedSentence));
        }

		return result;
    }

    public TokenizedSentence tokenize(UntokenizedSentence untokenizedSentence) {
        TokenizedSentence result = new TokenizedSentence();

        result.setStart(untokenizedSentence.get(0).getStart());
        result.setEnd(untokenizedSentence.get(untokenizedSentence.size() - 1).getEnd());

        Sentence tokenizedSentence = new Sentence();

        for (Word word : untokenizedSentence) {
            System.out.print(word.getText() + " ");
            LinkedList<Token> tokens = lexicon.get(word.getText());

            if(tokens.size() == 0) {
                result.addError(new TokenizingError(word.getStart(), word.getEnd()));
               // System.out.println("Could not find '" + word + "' in lexicon.");
                tokenizedSentence.add(new Token("UNDEF"));
                continue;
            }

            tokenizedSentence.add(tokens.get(0));
        }

        PossibleSentences possibleSentences = new PossibleSentences();
        possibleSentences.add(tokenizedSentence);

        result.setSentences(possibleSentences);

        return result;
    }

     private boolean isWhitespace(char c) {
        for(int i = 0; i < whitespace.length; i++) {
            if(whitespace[i] == c) {
                return true;
            }
        }

        return false;
    }


    private boolean isTerminator(char c) {
        for(int i = 0; i < terminators.length; i++) {
            if(terminators[i] == c) {
                return true;
            }
        }

        return false;

    }

    private boolean isSymbol(char c) {
        for(int i = 0; i < symbols.length; i++) {
            if(symbols[i] == c) {
                return true;
            }
        }

        return false;
    }

    public List<UntokenizedSentence> split(String sentance) {
        int cursor = 0;     // Position we are currently looking at.
        int lastToken = 0;  // The end of the last token consumed.

        List<UntokenizedSentence> sentences = new Vector<UntokenizedSentence>();
        UntokenizedSentence currentSentence = new UntokenizedSentence();


        while(cursor < sentance.length()) {
            char currentChar = sentance.charAt(cursor);

            if(isWhitespace(currentChar)) {
                // Add the word preceding the whitespace.
                if(cursor > lastToken) currentSentence.add(new Word(sentance.substring(lastToken, cursor), lastToken, cursor));

                // Find how much whitespace there is
                int whitespaceChars = 0;
                while((cursor + whitespaceChars) < sentance.length() && isWhitespace(sentance.charAt(cursor + whitespaceChars))) {
                    whitespaceChars++;
                }

                lastToken = cursor + whitespaceChars;
                cursor += whitespaceChars  -1;
            } else if(isTerminator(currentChar)) {
                // Run for president.
                
                if(cursor > lastToken) currentSentence.add(new Word(sentance.substring(lastToken, cursor), lastToken, cursor));
                lastToken = cursor + 1;
                currentSentence.add(new Word(Character.toString(sentance.charAt(cursor)), cursor, cursor + 1));
                sentences.add(currentSentence);

                currentSentence = new UntokenizedSentence();

            } else if(isSymbol(currentChar)) {
                if(cursor > lastToken) currentSentence.add(new Word(sentance.substring(lastToken, cursor), lastToken, cursor));
                lastToken = cursor + 1;
                currentSentence.add(new Word(Character.toString(sentance.charAt(cursor)), cursor, cursor + 1));
                
            } 
            cursor++;
        }
        if(!currentSentence.isEmpty()) {
            sentences.add(currentSentence);
        }
        return sentences;
    }

    private class UntokenizedSentence extends Vector<Word> {}
}
