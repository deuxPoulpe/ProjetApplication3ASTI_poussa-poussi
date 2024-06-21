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
    private Iterator<Set<Coordinates>> combinationsIterator;
    private CoordinateSetGridPair currentPair;

    public RemovGridIterator(Grid grid, List<List<Coordinates>> alignments) {
        currentPair = new CoordinateSetGridPair(null, grid);
        combinationsIterator = new CombinationIterator(alignments);
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
        Grid removGrid = currentPair.getGrid().clone();
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
        private int alignmentsSize;
        private int maxIndex = (int) Math.pow(binomialCoefficient(5, 2), alignmentsSize); // le nombre total de combinaisons possibles
        private int currentIndex;

        public CombinationIterator(List<List<Coordinates>> alignments) {
            this.alignments = alignments;
            this.alignmentsSize = alignments.size();
            this.currentIndex = 0;
        }

        @Override
        public boolean hasNext() {
            // On vérifie si l'index courant est inférieur à 2C5^n où n est la taille de la liste d'alignements.
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
         * Calcule le coefficient binomial C(n, k) en utilisant la programmation dynamique.
         *
         * L'algorithme utilise un tableau dp pour stocker les coefficients binomiaux intermédiaires.
         * Pour chaque i de 1 à n, il met à jour le tableau dp pour refléter les coefficients binomiaux de i.
         * Il utilise le fait que C(i, j) = C(i-1, j-1) + C(i-1, j) pour mettre à jour le tableau dp.
         *
         * Complexité temporelle : O(n*k), où n est le premier argument et k le second.
         * Complexité spatiale : O(k), où k est le second argument.
         *
         * @param n le nombre total d'éléments
         * @param k le nombre d'éléments à choisir
         * @return le coefficient binomial C(n, k)
         */
        private int binomialCoefficient(int n, int k) {
            int dp[] = new int[k + 1];

            // nC0 is 1
            dp[0] = 1;  

            for (int i = 1; i <= n; i++) {
                // Compute next row of pascal triangle using the previous row
                for (int j = Math.min(i, k); j > 0; j--)
                    dp[j] = dp[j] + dp[j-1];
            }
            return dp[k];
        }

    }
}
