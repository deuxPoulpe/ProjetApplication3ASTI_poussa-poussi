package actions;

import java.util.Set;

public class Removal {
    
    private Set<int[]> combination;

    public Removal(Set<int[]> combination) {
        this.combination = combination;
    }

    public Set<int[]> getCombination() {
        return combination;
    }

    public String toString() {
        return "Removal: " + combination;
    }
}
