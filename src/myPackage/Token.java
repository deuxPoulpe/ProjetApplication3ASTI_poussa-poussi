package myPackage;

import java.util.List;
import java.util.ArrayList;

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
        this.alignments = new ArrayList<int[]>();
    }

    public Token(char myColour, List<int[]> alignments) {
        this.colour = myColour;
        this.alignments = alignments;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Token token = (Token) obj;
        return this.colour == token.getColour();
    }
}