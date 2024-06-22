package treeFormationPackage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import gamePackage.Coordinates;
import gamePackage.Grid;

public class RemovGridIterator implements Iterator<CoordinateSetGridPair> {
    private Grid grid;
    private Iterator<Set<Coordinates>> combinationsIterator;
    private CoordinateSetGridPair currentPair;

    public RemovGridIterator(Grid grid, List<List<Coordinates>> alignments) {
        currentPair = new CoordinateSetGridPair(null, grid);
        combinationsIterator = new CombinationIterator(alignments);
        this.grid = grid;
    }

    @Override
    public boolean hasNext() {
        return combinationsIterator != null && combinationsIterator.hasNext();
    }

    @Override
    public CoordinateSetGridPair next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }


        // On itère sur la prochaine combinaison.
        Set<Coordinates> combination = combinationsIterator.next();

        // On retire les jetons aux coordonnées de la combinaison actuelle.
        Grid removGrid = grid.clone();
        for (Coordinates coord : combination) {
            removGrid.removeToken(coord);
        }

        // On met à jour la grille courante et la combinaison courante.
        currentPair.setGrid(removGrid);
        currentPair.setCoordinates(combination);

        return currentPair;
    }

    private class CombinationIterator implements Iterator<Set<Coordinates>> {
        private List<List<Coordinates>> alignments;
        private int[] combinationIndices;
        private boolean hasNext = true;
    
        public CombinationIterator(List<List<Coordinates>> alignments) {
            this.alignments = alignments;
            this.combinationIndices = new int[alignments.size()];
        }
    
        @Override
        public boolean hasNext() {
            return hasNext;
        }
    
        @Override
        public Set<Coordinates> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
    
            Set<Coordinates> combinations = new HashSet<>();
            for (int i = 0; i < alignments.size(); i++) {
                List<Set<Coordinates>> currentCombinations = getCombinations(alignments.get(i), 2);
                combinations.addAll(currentCombinations.get(combinationIndices[i]));
            }
    
            incrementIndices();
            return combinations;
        }
    
        private void incrementIndices() {
            for (int i = 0; i < combinationIndices.length; i++) {
                combinationIndices[i]++;
                if (combinationIndices[i] < getCombinations(alignments.get(i), 2).size()) {
                    return; // Pas besoin d'incrémenter les indices suivants.
                }
                combinationIndices[i] = 0; // Réinitialiser cet indice et incrémenter le suivant.
            }
            hasNext = false; // Tous les indices ont été réinitialisés, ce qui signifie que toutes les combinaisons ont été générées.
        }
    
        private List<Set<Coordinates>> getCombinations(List<Coordinates> list, int k) {
            List<Set<Coordinates>> combinations = new ArrayList<>();
            getCombinationsHelper(list, k, 0, new HashSet<>(), combinations);
            return combinations;
        }
    
        private void getCombinationsHelper(List<Coordinates> list, int k, int startIndex, Set<Coordinates> current, List<Set<Coordinates>> result) {
            if (current.size() == k) {
                result.add(new HashSet<>(current));
                return;
            }
    
            for (int i = startIndex; i < list.size(); i++) {
                current.add(list.get(i));
                getCombinationsHelper(list, k, i + 1, current, result);
                current.remove(list.get(i));
            }
        }
    }
}
