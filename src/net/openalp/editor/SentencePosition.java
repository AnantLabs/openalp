package net.openalp.editor;

/**
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class SentencePosition {
    int start, end;
    boolean complete;

    public SentencePosition(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getLength() {
        return end - start;
    }
}