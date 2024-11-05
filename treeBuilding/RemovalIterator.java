package treeBuilding;

import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import game.Board;
import actions.Removal;

public class RemovalIterator implements Iterator<Removal> {
    private List<List<int[]>> alignments;
    private int[] combinationIndices;
    private boolean hasNext = true;
    private int k = 2;

    public RemovalIterator(Board board, int players) {
        this.alignments = board.getAlignments(players, 5);
        this.combinationIndices = new int[alignments.size()];
        
        // Check if there are any valid combinations to begin with
        this.hasNext = checkInitialHasNext();
    }
    
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Removal next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }

        Set<int[]> combinations = new HashSet<>();
        for (int i = 0; i < alignments.size(); i++) {
            combinations.addAll(getSingleCombination(alignments.get(i), k, combinationIndices[i]));
        }

        incrementIndices();
        return new Removal(combinations);
    }

    private void incrementIndices() {
        for (int i = 0; i < combinationIndices.length; i++) {
            combinationIndices[i]++;
            if (combinationIndices[i] < alignments.get(i).size() - k + 1) {
                return;
            }
            combinationIndices[i] = 0;
        }
        hasNext = false;
    }

    /**
     * Retrieves a single combination of `k` elements starting at the specified index.
     */
    private Set<int[]> getSingleCombination(List<int[]> list, int k, int startIndex) {
        Set<int[]> combination = new HashSet<>();
        for (int i = 0; i < k; i++) {
            combination.add(list.get((startIndex + i) % list.size()));
        }
        return combination;
    }

    private boolean checkInitialHasNext() {
        if (alignments.isEmpty()) {
            return false;
        }
        for (List<int[]> alignment : alignments) {
            if (alignment.size() < k) {
                return false;
            }
        }
        return true;
    }
}
