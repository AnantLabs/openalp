package net.openalp.core;


/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class ParseResult {
    private float validity;
    private TokenizingResult tokenizingResult = new TokenizingResult();

    ParseResult() {
    }

    public ParseResult(float validity) {
        this.validity = validity;
    }

    public TokenizingResult getTokenizingResult() {
        return tokenizingResult;
    }

    public void setTokenizingResult(TokenizingResult result) {
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
}
