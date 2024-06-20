package iteratorsPackage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import myPackage.CoordinateSetGridPair;
import myPackage.Coordinates;
import myPackage.Grid;

public class RemovGridIterator implements Iterator<CoordinateSetGridPair> {
    private Iterator<Set<Coordinates>> combinationsIterator;
    private CoordinateSetGridPair currentPair;

    public RemovGridIterator(Grid grid, List<List<Coordinates>> alignments) {
        this.currentPair.setGrid(grid);
        this.currentPair.setCoordinates(new HashSet<>());
        combinationsIterator = new CombinationIterator(alignments);
    }

    @Override
    public boolean hasNext() {
        if (combinationsIterator != null && combinationsIterator.hasNext()) {
            return true;
        }
        return false;
    }

    @Override
    public CoordinateSetGridPair next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // On itère sur la prochaine combinaison.
        Set<Coordinates> combination = combinationsIterator.next();

        // On retire les jetons aux coordonnées de la combinaison actuelle.
        Grid removGrid = currentPair.getGrid().clone();
        for (Coordinates coord : combination) {
            removGrid.removeToken(coord);
        }

        // On met à jour la grille courante et la combinaison courante.
        currentPair.setGrid(removGrid);
        currentPair.setCoordinates(combination);

        return currentPair;
    }

    public class CombinationIterator implements Iterator<Set<Coordinates>> {
        private List<List<Coordinates>> alignments;
        private int alignmentsSize;
        private int maxIndex = binomialCoefficient(alignmentsSize, 2); // le nombre total de combinaisons possibles
        private int currentIndex;

        public CombinationIterator(List<List<Coordinates>> alignments) {
            this.alignments = alignments;
            this.alignmentsSize = alignments.size();
            this.currentIndex = 0;
        }

        @Override
        public boolean hasNext() {
            // On vérifie si l'index courant est inférieur à 2Cn où n est la taille de la liste d'alignements.
            return currentIndex < maxIndex;
        }

        @Override
        public Set<Coordinates> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // On place une combinaison de 2 jetons de chaque alignement dans l'ensemble de coordonnées.
            Set<Coordinates> combinations = new HashSet<>();
            for (int i = 0; i < alignmentsSize; i++) {
                combinations.addAll(getCombinations(alignments.get(i), 2).get(currentIndex));
            }

            // On incrémente l'index courant pour préparer la prochaine combinaison de deux jetons par alignement.
            currentIndex++;
            return combinations;
        }

        /**
         * Cette méthode est une fonction d'aide récursive pour générer toutes les combinaisons possibles de taille k à partir d'une liste donnée.
         * Elle utilise une approche de backtracking pour générer toutes les combinaisons.
         *
         * @param list la liste à partir de laquelle générer les combinaisons.
         * @param k la taille des combinaisons à générer.
         * @param startIndex l'index à partir duquel commencer à générer les combinaisons.
         * @param current l'ensemble actuel de coordonnées qui forme une combinaison.
         * @param result la liste de tous les ensembles de coordonnées qui forment une combinaison.
         * 
         * Complexité : O(nCk) où n est la taille de la liste et k est la taille des combinaisons à générer.
         */
        private void getCombinationsHelper(List<Coordinates> list, int k, int startIndex, Set<Coordinates> current, List<Set<Coordinates>> result) {
            // Si la taille de l'ensemble actuel est égale à k, alors nous avons une combinaison valide.
            // Nous l'ajoutons à la liste des résultats et retournons.
            if (current.size() == k) {
                result.add(new HashSet<>(current));
                return;
            }

            // Nous parcourons la liste à partir de l'index de départ.
            for (int i = startIndex; i < list.size(); i++) {
                // Nous ajoutons l'élément actuel à l'ensemble actuel.
                current.add(list.get(i));

                // Nous appelons récursivement la fonction avec l'index de départ incrémenté de 1.
                getCombinationsHelper(list, k, i + 1, current, result);

                // Après l'appel récursif, nous retirons l'élément actuel de l'ensemble actuel pour le backtracking.
                current.remove(list.get(i));
            }
        }

        /**
         * Génère toutes les combinaisons possibles de taille k à partir d'une liste donnée.
         * 
         * @param list la liste à partir de laquelle générer les combinaisons.
         * @param k la taille des combinaisons à générer.
         * @return une liste de toutes les combinaisons possibles.
         * 
         * Complexité : O(nCk) où n est la taille de la liste et k est la taille des combinaisons à générer.
         */
        private List<Set<Coordinates>> getCombinations(List<Coordinates> list, int k) {
            List<Set<Coordinates>> combinations = new ArrayList<>();
            getCombinationsHelper(list, k, 0, new HashSet<>(), combinations);
            return combinations;
        }

        /**
         * Calcule le coefficient binomial, également connu sous le nom de "nombre de combinaisons" ou "combinaison de n parmi k".
         * Le coefficient binomial est le nombre de façons de choisir k éléments parmi un ensemble de n éléments, sans tenir compte de l'ordre.
         * 
         * @param n le nombre total d'éléments
         * @param k le nombre d'éléments à choisir
         * @return le coefficient binomial, c'est-à-dire le nombre de façons de choisir k éléments parmi n
         * 
         * Complexité : O(nCk) où n est le nombre total d'éléments et k est le nombre d'éléments à choisir.
         */
        private int binomialCoefficient(int n, int k) {
            // Si k est 0 ou égal à n, il n'y a qu'une seule façon de choisir les éléments
            if (k == 0 || k == n) {
                return 1;
            }
            // Sinon, le coefficient binomial peut être calculé en utilisant la formule récursive :
            // C(n, k) = C(n-1, k-1) + C(n-1, k)
            return binomialCoefficient(n - 1, k - 1) + binomialCoefficient(n - 1, k);
        }

    }
}
