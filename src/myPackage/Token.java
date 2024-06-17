package myPackage;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private char colour;
    private List<int[]> alignments;

    public char getColour() {
        return this.colour;
    }

    public void setColour(char myColour) {
        this.colour = myColour;
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

    public Token(char myColour) {
        this.colour = myColour;
        this.alignments = new ArrayList<>();
    }

    public Token(char myColour, List<int[]> alignments) {
        this.colour = myColour;
        this.alignments = alignments;
    }
}