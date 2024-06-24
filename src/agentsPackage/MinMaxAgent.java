package agentsPackage;

import java.util.Arrays;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Settings;
import treeFormationPackage.ActionIterator;
import treeFormationPackage.ActionTree;
import gamePackage.Action;

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

        System.out.println("Executing action: " + action);

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
        ActionIterator childIterator = new ActionIterator(node);

        // Vérifie si le noeud est une feuille ou si la profondeur est nulle, puis retourne le noeud
        if (depth == 0 || !childIterator.hasNext()) {
            node.calculateHeuristicValue();
            return node;
        }

        if (maximizingPlayer) {
            ActionTree bestChild = null;
            int maxEval = Integer.MIN_VALUE;

            while (childIterator.hasNext()) {
                ActionTree child = childIterator.next(); // Correction: déplacement de cette ligne dans la boucle

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

        } else { // Tour du joueur minimisant
            ActionTree bestChild = null;
            int minEval = Integer.MAX_VALUE;

            while (childIterator.hasNext()) {
                ActionTree child = childIterator.next(); // Correction: déplacement de cette ligne dans la boucle

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
    }
}
