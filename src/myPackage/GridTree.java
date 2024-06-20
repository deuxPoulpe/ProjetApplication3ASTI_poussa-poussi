package myPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GridTree {

    private List<GridTree> children = new ArrayList<>();
    private Grid grid;
    private Coordinates placeCoordinates;
    private PushAction pushAction;
    private List<Set<Coordinates>> removCoordinates = new ArrayList<>(); // Première liste pour les coordonnées des jetons à retirer en début de tour, deuxième pour les jetons à retirer en fin de tour
    private int pointCounter = 0;
    private int heuristicValue = 0;
    private MinMaxAgent agent;

    // Constructeur pour la racine de l'arbre
    public GridTree(MinMaxAgent agent, Grid grid) {
        this.agent = agent;
        this.grid = grid;
        this.removCoordinates.add(new HashSet<>());
        this.removCoordinates.add(new HashSet<>());
    }

    // Constructeur pour les noeuds de l'arbre
    public GridTree(GridTree parent, Grid grid, Coordinates placeCoordinates, PushAction pushAction) {

        // attributs héréditaires
        this.pointCounter = parent.pointCounter;
        this.agent = parent.agent;
        this.removCoordinates.add(parent.removCoordinates.get(0));

        // attributs spécifiques
        this.removCoordinates.add(new HashSet<>());
        this.grid = grid;
        this.placeCoordinates = placeCoordinates;
        this.pushAction = pushAction;
    }

    public String toString() {
        String strRemovCoordinates1 = "Tokens to remove first: " + removCoordinates.get(0) + "\n";
        String strPlaceCoordinates = "Place coordinates: " + placeCoordinates + "\n";
        String strPushAction = "Push action: " + pushAction;
        String strRemovCoordinates2 = "Tokens to remove second: " + removCoordinates.get(1);

        return strRemovCoordinates1 + strPlaceCoordinates + strPushAction + strRemovCoordinates2;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public Coordinates getPlaceCoordinates() {
        return placeCoordinates;
    }

    public PushAction getPushAction() {
        return pushAction;
    }

    public List<Set<Coordinates>> getRemovCoordinates() {
        return removCoordinates;
    }

    public void setRemovCoordinates(List<Set<Coordinates>> removCoordinates) {
        this.removCoordinates = removCoordinates;
    }

    public List<GridTree> getChildren() {
        return children;
    }

    public void addChild(GridTree child) {
        children.add(child);
    }

    public boolean isLeaf() {
        return children == null;
    }

    public int calculateScore(int[] alignmentCount, int[] opponentAlignmentCount) {
        int score = 0;
        for (int i = 0; i < 4; i++) {
            score += (alignmentCount[i] - opponentAlignmentCount[i]) * agent.getWeights()[i];
        }
        return score;
    }

    public void calculateHeuristicValue() {
        int[] alignmentCounts = new int[4];
        int[] opponentAlignmentCounts = new int[4];

        // On récupère les alignements de chaque joueur
        for (int i = 2; i < 6; i++) {
            // joueur courant
            alignmentCounts[i - 2] = grid.getAlignments(agent.getColor(), i).size();

            // adversaire
            if (agent.getColor() == 'B') {
                opponentAlignmentCounts[i - 2] = grid.getAlignments('Y', i).size();
            } else {
                opponentAlignmentCounts[i - 2] = grid.getAlignments('B', i).size();
            }
        }

        // On ajoute le score des alignements de 5 jetons formés après la poussée
        pointCounter += removCoordinates.get(1).size() / 2;

        // On calcule la valeur heuristique des alignements de 2, 3, 4 et 5 jetons
        int alignmentsScore = calculateScore(alignmentCounts, opponentAlignmentCounts);

        // on calcule la valeur heuristique
        heuristicValue = pointCounter * agent.getWeights()[3] + alignmentsScore;
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
     */
    private List<Set<Coordinates>> getCombinations(List<Coordinates> list, int k) {
        List<Set<Coordinates>> combinations = new ArrayList<>();
        getCombinationsHelper(list, k, 0, new HashSet<>(), combinations);
        return combinations;
    }

    /**
     * Renvoie les grilles obtenues après avoir retiré deux jetons de chaque alignement de 5 jetons du joueur.
     * 
     * @param inputGrid la grille sur laquelle retirer les jetons.
     * @return HashMap<Set<Coordinates>, Grid> contient toutes les combinaisons de deux jetons à retirer par alignement et la grille obtenue après les avoir retirés.
     */
    public HashMap<Set<Coordinates>, Grid> getRemovMap(Grid inputGrid) {
        HashMap<Set<Coordinates>, Grid> gridMap = new HashMap<>();
        
        // Pour chaque alignement de 5 jetons du joueur
        List<List<Coordinates>> alignments = inputGrid.getAlignments(agent.getColor(), 5);
        for (List<Coordinates> alignment : alignments) {

            // Générer toutes les combinaisons de deux jetons à retirer
            List<Set<Coordinates>> combinations = getCombinations(alignment, 2);
            for (Set<Coordinates> combination : combinations) {

                // Retirer les jetons de la combinaison dans une copie du plateau
                Grid removGrid = inputGrid.clone();
                for (Coordinates coord : combination) {
                    removGrid.removeToken(coord);
                }

                // Ajouter la grille obtenue après avoir retiré les jetons de la combinaison à la map
                gridMap.put(combination, removGrid);
            }
        }
        return gridMap;
    }

    private GridTree createPlaceChild(Grid grid, Coordinates coords) {
        // On inialise un clone du plateau avec le jeton placé
        Grid placeGrid = grid.clone();
        placeGrid.placeToken(agent.getColor(), coords);
        return new GridTree(this, placeGrid, coords, null);
    }

    private List<GridTree> getPlaceChildren() {

        List<GridTree> childList = new ArrayList<>();

        // On récupére la liste des jetons à retirer
        HashMap<Set<Coordinates>, Grid> removGrids = getRemovMap(grid);

        // S'il n'y a pas de jetons à retirer
        if (removGrids.isEmpty()) {
            // Pour chaque cellule vide
            List<Coordinates> emptyCells = grid.getValidEmptyCells();
            for (Coordinates emptyCellCoords : emptyCells) {
                childList.add(createPlaceChild(grid, emptyCellCoords));
            }
        } else {
            // Pour chaque ensemble de jetons à retirer
            for (Set<Coordinates> coordsToRemoveSet : removGrids.keySet()) {
                // Pour chaque cellule vide de la grille obtenue après avoir retiré les jetons
                Grid currentGrid = removGrids.get(coordsToRemoveSet);
                List<Coordinates> emptyCells = currentGrid.getValidEmptyCells();
                for (Coordinates emptyCellCoords : emptyCells) {
                    childList.add(createPlaceChild(currentGrid, emptyCellCoords));
                }
            }
            
        }
        return childList;
    }

    private List<GridTree> getPushChildren() {
            
        List<GridTree> childList = new ArrayList<>();

        // Pour chaque jeton du joueur sur le plateau
        List<Coordinates> ownTokens = agent.getOwnTokensCoords(grid);
        for (Coordinates ownTokenCoords : ownTokens) {

            // Pour chaque direction de poussée valide
            List<int[]> validDirections = agent.getValidPushDirections(grid, ownTokenCoords);
            for (int[] direction : validDirections) {

                // On effectue la poussée sur une copie du plateau
                Grid pushGrid = grid.clone();
                pushGrid.pushToken(agent.getColor(), ownTokenCoords, direction);
                PushAction pushAction = new PushAction(ownTokenCoords, direction);

                // On récupère les alignements de 5 jetons du joueur après la poussée
                HashMap<Set<Coordinates>, Grid> removGrids = getRemovMap(pushGrid);
                
                // Si aucun alignement de 5 de notre couleur n'a été formé, on ajoute la grille en tant que fils du noeud
                if (removGrids.isEmpty()) {
                    GridTree child = new GridTree(this, pushGrid, placeCoordinates , pushAction);
                    childList.add(child);

                // Sinon, on ajoute les grilles obtenues après avoir retiré tous les jetons à retirer en tant que fils du noeud
                } else { 
                    for (Set<Coordinates> coordsToRemoveSet : removGrids.keySet()) {
                        Grid currentGrid = removGrids.get(coordsToRemoveSet);
                        GridTree child = new GridTree(this, currentGrid, placeCoordinates, pushAction);
                        child.removCoordinates.get(1).addAll(coordsToRemoveSet);
                        childList.add(child);
                    }
                }
            }
        }
        return childList;
    }

    public void generateChildNodes() {

        // On génère les fils pour placer un jeton
        for (GridTree placeChild : getPlaceChildren()) {
            // On génère les fils pour pousser un jeton
            if (!Settings.getInstance().getMandatoryPush())
                addChild(placeChild);
            for (GridTree pushChild : placeChild.getPushChildren()) {
                // On ajoute le fils à la liste des enfants du noeud courant
                addChild(pushChild);
            }
        }

    }

}

// Note : remplacer getRemovMap par getRemovChildren ???