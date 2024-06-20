package myPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

import agentsPackage.MinMaxAgent;
import iteratorsPackage.ChildIterator;

public class GridTree {

    // Caractéristiques du noeud
    private MinMaxAgent agent;
    private Grid grid;
    private ChildIterator childIterator;
    private int heuristicValue = 0;
    private int pointCounter = 0;

    // Actions du noeud
    private Coordinates placeCoordinates;
    private PushAction pushAction;
    private List<Set<Coordinates>> removCoordinates = new ArrayList<>(); // Première liste pour les coordonnées des jetons à retirer en début de tour, deuxième pour les jetons à retirer en fin de tour
    

    // Constructeur pour la racine de l'arbre
    public GridTree(MinMaxAgent agent, Grid grid) {
        this.agent = agent;
        this.grid = grid;
        this.removCoordinates.add(new HashSet<>());
        this.removCoordinates.add(new HashSet<>());
        this.childIterator = new ChildIterator(this);
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
        this.childIterator = new ChildIterator(this);
    }

    public String toString() {
        String strRemovCoordinates1 = "Tokens to remove first: " + removCoordinates.get(0) + "\n";
        String strPlaceCoordinates = "Place coordinates: " + placeCoordinates + "\n";
        String strPushAction = "Push action: " + pushAction;
        String strRemovCoordinates2 = "Tokens to remove second: " + removCoordinates.get(1);

        return strRemovCoordinates1 + strPlaceCoordinates + strPushAction + strRemovCoordinates2;
    }

    public Grid getGrid() {
        return grid;
    }

    public MinMaxAgent getAgent() {
        return agent;
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

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setRemovCoordinates(List<Set<Coordinates>> removCoordinates) {
        this.removCoordinates = removCoordinates;
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
     * Génère les fils du noeud courant.
     * 
     * Ajoute les fils pour placer un jeton, puis les fils pour pousser un jeton.
     */
    public GridTree getNextChild() {
        try {
            return childIterator.next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}