package agentsPackage;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import gamePackage.Settings;
import java.util.Arrays;
import java.util.Set;
import treeFormationPackage.ActionTree;
import treeFormationPackage.ChildIterator;

public class MinMaxAgent extends Agent{

    private int smartness;
    private final int[] weights = {1, 3, 9, 50};
    
    public MinMaxAgent(char myColor, int smartness) {
        super(myColor);
        this.smartness = smartness;
    }

    public int[] getWeights() {
        return weights;
    }

    public int getSmartness() {
        return smartness;
    }

    public void executeGameRound(Grid grid) {
        
        // Si le plateau est plein, on affiche un message d'erreur
        if (grid.getSize() * grid.getSize() == grid.getHashMap().keySet().size()) System.out.println("No possible moves");
        
        // Calcule le meilleur coup à jouer
        ActionTree root = new ActionTree(this, grid);
        ActionTree bestMove = evaluateBestMove(root, smartness, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        
        // Phase de retrait 1

        String message ="";

        // Pour chaque alignement de 5 jetons du joueur formé par l'adversaire, on retire 2 jetons de l'alignement
        for (Coordinates removCoords : bestMove.getRemovCoordinates().get(0)) {
            grid.removeToken(removCoords);
            message += "Removed token at " + removCoords.toString() + "\n";
        }

        if (Settings.getInstance().getDisplayInTerminal())
            System.out.println(message);

        // Phase de placement

        // Place le jeton sur le plateau
        grid.placeToken(getColor(), bestMove.getPlaceCoordinates());
        
        if (Settings.getInstance().getDisplayInTerminal()) {
            System.out.println(getColor() + " : places token at " + bestMove.getPlaceCoordinates().toString());
            grid.display();
        }
        
        // Phase de poussée

        PushAction pushAction = bestMove.getPushAction();

        // Si le plateau n'est pas plein, on pousse le jeton choisi dans la direction choisie
        if (grid.isFull()){
            message = "The grid is full. No more tokens can be pushed.";
        } else if (pushAction == null) {
            message = getColor() + " : does not push any token";
        } else {
            message = getColor() + " : pushes token at " + pushAction.getCoordinates().toString() + " in direction " + Arrays.toString(pushAction.getDirection());
            System.out.println(message);
            grid.pushToken(bestMove.getPushAction(), getColor());
        }
        if (Settings.getInstance().getDisplayInTerminal())

        // Phase de retrait 2

        message = "";

        // Pour chaque alignement de 5 jetons formé, on retire 2 jetons de l'alignement
        Set<Coordinates> removCoordSet = bestMove.getRemovCoordinates().get(1);

        if (removCoordSet != null) {
            for (Coordinates removCoords : removCoordSet) {
                grid.removeToken(removCoords);
                message += "Removed token at " + removCoords.toString() + "\n";
            }
        }

        if (Settings.getInstance().getDisplayInTerminal()) {
            System.out.println(message);
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
    public ActionTree evaluateBestMove(ActionTree node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        ChildIterator childIterator = new ChildIterator(node);

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

                // Définit la profondeur du noeud enfant
                child.setDepth(node.getDepth() + 1);

                // Évalue le noeud enfant
                ActionTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, false);
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
                ActionTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, true);
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
