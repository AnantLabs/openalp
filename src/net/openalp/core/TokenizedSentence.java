package net.openalp.core;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class TokenizedSentence {
    private PossibleSentences sentences;
    private List<TokenizingError> errors = new LinkedList<TokenizingError>();
    private int start, end;

    public TokenizedSentence() {
    }

    public TokenizedSentence(PossibleSentences sentences, List<TokenizingError> errors) {
        this.sentences = sentences;
        this.errors = errors;
    }

    public List<TokenizingError> getErrors() {
        return errors;
    }

    public void addError(TokenizingError error) {
        errors.add(error);
    }

    public PossibleSentences getSentences() {
        return sentences;
    }

    public void setSentences(PossibleSentences sentences) {
        this.sentences = sentences;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
