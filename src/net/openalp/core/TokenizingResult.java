package net.openalp.core;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class TokenizingResult {
    private List<Sentence> sentences;
    private List<TokenizingError> errors = new LinkedList<TokenizingError>();

    public TokenizingResult() {
    }

    public TokenizingResult(List<Sentence> sentences, List<TokenizingError> errors) {
        this.sentences = sentences;
        this.errors = errors;
    }

    public List<TokenizingError> getErrors() {
        return errors;
    }

    public void addError(TokenizingError error) {
        errors.add(error);
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }
}
