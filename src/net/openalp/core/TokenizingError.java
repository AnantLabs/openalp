package net.openalp.core;

/**
 * An error while parsing.
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class TokenizingError {
    private int start, end;

    public TokenizingError(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getLength() {
        return end - start;
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
