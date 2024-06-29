package treeFormationPackage;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import agentsPackage.MinMaxAgent;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Action;

public class ActionTree {

    // Caractéristiques du noeud
    private MinMaxAgent agent;
    private int heuristicValue = 0;
    private int depth = 0;
    private Action action;


    // Constructeur pour la racine de l'arbre
    public ActionTree(MinMaxAgent agent, Grid myGrid) {
        this.agent = agent;
        this.depth = 0;
        this.action = new Action(new HashSet<>(), null, null, new HashSet<>(), myGrid);
    }

    // Constructeur pour les noeuds de l'arbre
    public ActionTree(ActionTree parent, Action myAction) {

        this.agent = parent.agent;
        this.action = myAction;
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

    public void incrementDepth() {
        depth++;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    private int calculateScore(int[] alignmentCount, int[] opponentAlignmentCount) {
        int score = 0;
        for (int i = 0; i < 3; i++) {
            score += (alignmentCount[i] - opponentAlignmentCount[i]) * agent.getWeights()[i];
        }
        return score;
    }

    public void calculateHeuristicValue() {
        Grid grid = action.getGrid();

        int[] alignmentCounts = new int[3]; // Correction : tableau de taille 3 pour les alignements de 2, 3, et 4
        int[] opponentAlignmentCounts = new int[3]; // Correction : même chose pour l'adversaire
        List<List<List<Coordinates>>> alignmentsList = new ArrayList<>();
        List<List<List<Coordinates>>> opponentAlignmentsList = new ArrayList<>();

        // On récupère les alignements de chaque joueur
        for (int i = 4; i > 1; i--) {

            // joueur courant
            List<List<Coordinates>> alignments = grid.getAlignments(agent.getColor(), i);

            // On filtre les alignements contenant des jetons déjà présents dans d'autres alignements déjà comptés
            grid.filterAlignments(alignments);

            // On ajoute les alignements de 2, 3 et 4 jetons à la liste et on marque les jetons comme alignés dans leur direction
            alignmentsList.add(alignments);
            alignmentCounts[i - 2] = alignments.size(); // Correction : Utilisation directe de la taille de `alignments`
            for (List<Coordinates> alignment : alignments) {
                grid.markAlignment(alignment);
            }

            // adversaire
            char opponentColor = agent.getColor() == 'Y' ? 'B' : 'Y';
            List<List<Coordinates>> opponentAlignments = grid.getAlignments(opponentColor, i);
            grid.filterAlignments(opponentAlignments); // Correction : Filtrage des alignements de l'adversaire
            opponentAlignmentsList.add(opponentAlignments);
            opponentAlignmentCounts[i - 2] = opponentAlignments.size(); // Correction : Utilisation directe de la taille de `opponentAlignments`
            for (List<Coordinates> alignment : opponentAlignments) {
                grid.markAlignment(alignment);
            }
        }
        
        // On nettoie tous nos alignements de 5 jetons
        for (List<List<Coordinates>> alignments : alignmentsList) {
            grid.clearAlignments(alignments);
        }

        // On nettoie tous les alignements de 5 jetons de l'adversaire
        for (List<List<Coordinates>> alignments : opponentAlignmentsList) {
            grid.clearAlignments(alignments);
        }

        // On ajoute le score des alignements de 5 jetons formés après la poussée
        int pointCounter = action.getEndRemove().size() / 2 + action.getStartRemove().size() / 2;

        // On calcule la valeur heuristique des alignements de 2, 3, 4 et 5 jetons
        int alignmentsScore = calculateScore(alignmentCounts, opponentAlignmentCounts);

        // on calcule la valeur heuristique
        heuristicValue = pointCounter * agent.getWeights()[3] + alignmentsScore;
    }

}