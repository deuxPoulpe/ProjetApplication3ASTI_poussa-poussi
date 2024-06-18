package myPackage;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private char color;
    private List<int[]> alignments;

    public char getColor() {
        return this.color;
    }

    public void setColor(char myColor) {
        this.color = myColor;
    }

    public List<int[]> getAlignments() {
        return this.alignments;
    }

    public void setAlignment(List<int[]> alignments) {
        this.alignments = alignments;
    }

    public void addToAlignments(int[] alignment) {
        this.alignments.add(alignment);
    }

    public Token(char myColor) {
        this.color = myColor;
        this.alignments = new ArrayList<>();
    }

    public Token(char myColor, List<int[]> alignments) {
        this.color = myColor;
        this.alignments = alignments;
    }
}