package treeFormationPackage;

import java.util.HashSet;

import agentsPackage.MinMaxAgent;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import gamePackage.Action;

public class ActionTree {

    // Caractéristiques du noeud
    private MinMaxAgent agent;
    private int heuristicValue = 0;
    private int pointCounter = 0;
    private int depth = 0;
    private Action action;


    // Constructeur pour la racine de l'arbre
    public ActionTree(MinMaxAgent agent, Grid myGrid) {
        this.agent = agent;
        this.depth = 0;
        this.action = new Action(new HashSet<>(), null, null, new HashSet<>(), myGrid);
    }

    // Constructeur pour les noeuds de l'arbre
    public ActionTree(ActionTree parent, Grid myGrid, Coordinates placeCoordinates, PushAction pushAction) {

        this.pointCounter = parent.pointCounter;
        this.agent = parent.agent;
        this.action = new Action(parent.action.getStartRemove(), placeCoordinates, pushAction, new HashSet<>(), myGrid);
    }

    @Override
    public String toString() {
        return action.toString() + "Depth: " + depth + "\n";
    }

    public int getDepth() {
        return depth;
    }

    public Action getAction() {
        return action;
    }

    public MinMaxAgent getAgent() {
        return agent;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    private int calculateScore(int[] alignmentCount, int[] opponentAlignmentCount) {
        int score = 0;
        for (int i = 0; i < 4; i++) {
            score += (alignmentCount[i] - opponentAlignmentCount[i]) * agent.getWeights()[i];
        }
        return score;
    }

    public void calculateHeuristicValue() {
        Grid grid = action.getGrid();

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
        pointCounter += action.getEndRemove().size() / 2;

        // On calcule la valeur heuristique des alignements de 2, 3, 4 et 5 jetons
        int alignmentsScore = calculateScore(alignmentCounts, opponentAlignmentCounts);

        // on calcule la valeur heuristique
        heuristicValue = pointCounter * agent.getWeights()[3] + alignmentsScore;
    }

}