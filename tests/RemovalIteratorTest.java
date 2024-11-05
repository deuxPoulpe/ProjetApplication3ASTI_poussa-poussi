package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import game.Board;
import treeBuilding.RemovalIterator;

public class RemovalIteratorTest {
    
    public static void main(String[] args) {

        int nb_alignments = 0;

        // Create a new board
        Board board = new Board(8);
        for (int i = 0; i < nb_alignments; i++) {
            generateRandomAlignments(board);
        }

        // Generate all combinations of k elements per alignment
        int k = 2;
        List<Set<int[]>> combinations = new ArrayList<>();
        for (int i = 0; i < nb_alignments; i++) {
            // TODO
        }

        // Create a new removal iterator
        RemovalIterator removalIterator = new RemovalIterator(board, 2);

        List<Set<int[]>> generatedCombinations = new ArrayList<>();
        while (removalIterator.hasNext()) {
            generatedCombinations.add(removalIterator.next().getCombination());
        }

        if (combinations.equals(generatedCombinations)) {
            System.out.println("Test passed");
        } else {
            System.out.println("Test failed");
        }

    }

    public static void generateRandomAlignments(Board board) {
        // TODO
    }
}
