package treeFormationPackage;

import agentsPackage.MinMaxAgent;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionTree {

    // Caractéristiques du noeud
    private MinMaxAgent agent;
    private Grid grid;
    private int heuristicValue = 0;
    private int pointCounter = 0;
    private int depth = 0;

    // Actions du noeud
    private Coordinates placeCoordinates;
    private PushAction pushAction = null;
    private List<Set<Coordinates>> removCoordinates = new ArrayList<>(); // Première liste pour les coordonnées des jetons à retirer en début de tour, deuxième pour les jetons à retirer en fin de tour
    

    // Constructeur pour la racine de l'arbre
    public ActionTree(MinMaxAgent agent, Grid grid) {
        this.agent = agent;
        this.grid = grid;
        this.removCoordinates.add(new HashSet<>());
        this.removCoordinates.add(new HashSet<>());
        this.depth = 0;
    }

    // Constructeur pour les noeuds de l'arbre
    public ActionTree(ActionTree parent, Grid grid, Coordinates placeCoordinates, PushAction pushAction) {

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

    @Override
    public String toString() {
        String strRemovCoordinates1 = "Tokens to remove first: " + removCoordinates.get(0) + "\n";
        String strPlaceCoordinates = "Place coordinates: " + placeCoordinates + "\n";
        String strPushAction = "Push action: \n" + pushAction;
        String strRemovCoordinates2 = "Tokens to remove second: " + removCoordinates.get(1) + "\n";
        String strDepth = "Depth: " + depth + "\n";

        return strRemovCoordinates1 + strPlaceCoordinates + strPushAction + strRemovCoordinates2 + strDepth;
    }

    public int getDepth() {
        return depth;
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

    public void setPushAction(PushAction pushAction) {
        this.pushAction = pushAction;
    }

    public void setRemovCoordinates(List<Set<Coordinates>> removCoordinates) {
        this.removCoordinates = removCoordinates;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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
            alignmentCounts[i - 2] = grid.getAlignments(agent.getColor(), i).size();
            if (agent.getColor() == 'B') {
                opponentAlignmentCounts[i - 2] = grid.getAlignments('Y', i).size();
            } else {
                opponentAlignmentCounts[i - 2] = grid.getAlignments('B', i).size();
            }
        }
    
        System.out.println("Alignment Counts: " + Arrays.toString(alignmentCounts));
        System.out.println("Opponent Alignment Counts: " + Arrays.toString(opponentAlignmentCounts));
    
        pointCounter += removCoordinates.get(1).size() / 2;
    
        int alignmentsScore = calculateScore(alignmentCounts, opponentAlignmentCounts);
    
        heuristicValue = pointCounter * agent.getWeights()[3] + alignmentsScore;
    
    }

}