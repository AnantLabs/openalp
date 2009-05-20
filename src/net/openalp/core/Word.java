package net.openalp.core;

/**
 * A word before tokenization.
 *
 * @author Adam Scarr <scarr.adam@gmail.com>
 */
public class Word {
    private String text;
    private int start, end;

    public Word() {
    }

    public Word(String word, int start, int end) {
        this.text = word;
        this.start = start;
        this.end = end;
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

    public String getText() {
        return text;
    }

    public void setText(String word) {
        this.text = word;
    }

    public String toString() {
        return text;
    }

}
