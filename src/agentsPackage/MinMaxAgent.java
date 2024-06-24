package agentsPackage;

import gamePackage.Action;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Settings;
import java.util.*;
import treeFormationPackage.ActionIterator;
import treeFormationPackage.ActionTree;

public class MinMaxAgent extends Agent{

    private int smartness;
    private final int[] WEIGHTS = {1, 3, 9, 50};
    
    public MinMaxAgent(char myColor, int smartness) {
        super(myColor);
        this.smartness = smartness;
    }

    public int[] getWeights() {
        return WEIGHTS;
    }

    public int getSmartness() {
        return smartness;
    }

    @Override
    public void executeAction(Action action) {

        boolean displayInTerminal = Settings.getInstance().getDisplayInTerminal();
        Grid grid = action.getGrid();
        
        // Retire les jetons de l'alignement de 5 jetons de l'adversaire
        for (Coordinates removCoords : action.getStartRemove()) {
            grid.removeToken(removCoords);

            if (displayInTerminal) {
                System.out.println(getColor() + " removes token at " + removCoords.toString());
            }
        }
        if (action.getStartRemove().size() > 0 && displayInTerminal) {
            grid.display();
        }

        // Place le jeton sur le plateau
        if (action.getPlacement() != null) {
            grid.placeToken(getColor(), action.getPlacement());

            if (displayInTerminal) {
                System.out.println(getColor() + " places token at " + action.getPlacement().toString());
                grid.display();
            }
        }
        else {
            if (displayInTerminal) {
                System.out.println("The grid is full, "+ getColor() +"does not place any token");
            }
        }

        // Pousse le jeton si une poussée est possible
        if (action.getPush() != null) {
            grid.pushToken(action.getPush(), getColor());

            if (displayInTerminal) {
                System.out.println(getColor() + " pushes token at " + action.getPush().getCoordinates() + " in direction " + Arrays.toString(action.getPush().getDirection()));                grid.display();
                grid.display();
            }
        }
        else {
            if (displayInTerminal) {
                System.out.println(getColor() + " does not push any token");
            }
        }

        // Retire les jetons de l'alignement de 5 jetons du joueur
        for (Coordinates removCoords : action.getEndRemove()) {
            grid.removeToken(removCoords);

            if (displayInTerminal) {
                System.out.println(getColor() + " removes token at " + removCoords.toString());
            }

        }
        if (action.getEndRemove().size() > 0 && displayInTerminal) {
            grid.display();
        }
        
    }

    /**
     * Cette méthode permet d'évaluer le meilleur coup à jouer.
     * @param node
     * @param depth
     * @param alpha
     * @param beta
     * @param maximizingPlayer
     * @return GridTree
     */
    @Override
    public Action evaluateAction(Grid grid)  {
        ActionTree root = new ActionTree(this, grid);
        Action bestAction = findBestMove(root, smartness, Integer.MIN_VALUE, Integer.MAX_VALUE, true).getAction();
        bestAction.setGrid(grid);
        return bestAction;
    }

    private ActionTree findBestMove(ActionTree node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        List<ChildHeuristicPair> children = new ArrayList<>();
        ActionIterator childIterator = new ActionIterator(node);

        while (childIterator.hasNext()) {
            ActionTree child = childIterator.next();
            int heuristicValue = child.getHeuristicValue();
            children.add(new ChildHeuristicPair(child, heuristicValue));
        }

        // Trier les enfants selon leur valeur heuristique
        if (maximizingPlayer) {
            children.sort(Comparator.comparingInt(ChildHeuristicPair::getHeuristicValue).reversed());
        } else {
            children.sort(Comparator.comparingInt(ChildHeuristicPair::getHeuristicValue));
        }

        // Vérifie si le noeud est une feuille ou si la profondeur est nulle, puis retourne le noeud
        if (depth == 0 || children.isEmpty()) {
            node.calculateHeuristicValue();
            return node;
        }

        if (maximizingPlayer) {
            return maximize(node, depth, alpha, beta, children);
        } else {
            return minimize(node, depth, alpha, beta, children);
        }
    }

    private ActionTree maximize(ActionTree node, int depth, int alpha, int beta, List<ChildHeuristicPair> children) {
        ActionTree bestChild = null;
        int maxEval = Integer.MIN_VALUE;

        for (ChildHeuristicPair pair : children) {
            ActionTree child = pair.getChild();
            // Définit la profondeur du noeud enfant
            child.setDepth(node.getDepth() + 1);

            // Évalue le noeud enfant
            ActionTree nodeEval = findBestMove(child, depth - 1, alpha, beta, false);
            int eval = nodeEval.getHeuristicValue();

            // Met à jour la meilleure valeur et le meilleur enfant si nécessaire
            if (eval > maxEval) {
                maxEval = eval;
                bestChild = child;
            }

            // Met à jour alpha avec la valeur maximale entre alpha et eval
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) {
                break; // Coupe la branche si beta est inférieur ou égal à alpha
            }
        }
        return bestChild;
    }

    private ActionTree minimize(ActionTree node, int depth, int alpha, int beta, List<ChildHeuristicPair> children) {
        ActionTree bestChild = null;
        int minEval = Integer.MAX_VALUE;

        for (ChildHeuristicPair pair : children) {
            ActionTree child = pair.getChild();
            // Évalue le noeud enfant
            ActionTree nodeEval = findBestMove(child, depth - 1, alpha, beta, true);
            int eval = nodeEval.getHeuristicValue();

            // Met à jour la meilleure valeur et le meilleur enfant si nécessaire
            if (eval < minEval) {
                minEval = eval;
                bestChild = child;
            }

            // Met à jour beta avec la valeur minimale entre beta et eval
            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break; // Coupe la branche si beta est inférieur ou égal à alpha
            }
        }
        return bestChild;
    }

    // Classe auxiliaire pour stocker les paires enfant-valeur heuristique
    private static class ChildHeuristicPair {
        private final ActionTree child;
        private final int heuristicValue;

        public ChildHeuristicPair(ActionTree child, int heuristicValue) {
            this.child = child;
            this.heuristicValue = heuristicValue;
        }

        public ActionTree getChild() {
            return child;
        }

        public int getHeuristicValue() {
            return heuristicValue;
        }
    }
}
