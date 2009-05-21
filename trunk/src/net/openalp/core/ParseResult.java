package net.openalp.core;


/**
 * The result of parsing a sentance.
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class ParseResult {
    private float validity;
    private TokenizedSentence tokenizingResult = new TokenizedSentence();
    private int start, end;

    ParseResult() {
    }

    public ParseResult(float validity) {
        this.validity = validity;
    }

    public TokenizedSentence getTokenizingResult() {
        return tokenizingResult;
    }

    public void setTokenizingResult(TokenizedSentence result) {
        tokenizingResult = result;
    }

    public boolean isValid() {
        return validity > 0.0f;
    }

    public float getValidity() {
        return validity;
    }

    public void setValidity(float validity) {
        this.validity = validity;
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
