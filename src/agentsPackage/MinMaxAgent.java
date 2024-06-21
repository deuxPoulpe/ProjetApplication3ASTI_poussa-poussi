package agentsPackage;

import java.util.Set;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import gamePackage.Settings;
import treeFormationPackage.ChildIterator;
import treeFormationPackage.ActionTree;

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
            grid.pushToken(bestMove.getPushAction(), getColor());
            message = getColor() + " : pushes token at " + pushAction.getCoordinates().toString() + " in direction " + pushAction.getDirection().toString();
        }
        if (Settings.getInstance().getDisplayInTerminal())
            System.out.println(message);

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

        // Si le noeud est une feuille ou si la profondeur est nulle, on retourne le noeud
        if (depth == 0 || !childIterator.hasNext()) {
            node.calculateHeuristicValue();
            return node;
        }

        // Si c'est le tour du joueur maximisant
        if (maximizingPlayer) {
            ActionTree bestChild = null;

            // On initialise la meilleure valeur à un très petit nombre
            int maxEval = Integer.MIN_VALUE;

            // On parcourt le prochain enfant
            ActionTree child = childIterator.next();
            while (childIterator.hasNext()) {

                // On calcule la profondeur du noeud enfant
                child.setDepth(node.getDepth() + 1);

                // On évalue le noeud enfant
                ActionTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, false);

                // On récupère la valeur heuristique du noeud enfant
                int eval = nodeEval.getHeuristicValue();

                // On met à jour la meilleure valeur et le meilleur enfant
                if (eval > maxEval) {
                    maxEval = eval;
                    bestChild = child;
                }

                // Alpha prend la valeur du maximum entre alpha et la valeur de l'évaluation
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestChild;

        } else { // Si c'est le tour du joueur minimisant
            ActionTree bestChild = null;

            // On initialise la meilleure valeur à un très grand nombre
            int minEval = Integer.MAX_VALUE;

            ActionTree child = childIterator.next();
            while (childIterator.hasNext()) {
                ActionTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, true);

                // On récupère la valeur heuristique du noeud enfant
                int eval = nodeEval.getHeuristicValue();

                // On met à jour la meilleure valeur et le meilleur enfant
                if (eval < minEval) {
                    minEval = eval;
                    bestChild = child;
                }

                // Beta prend la valeur du minimum entre beta et la valeur de l'évaluation
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestChild;
        }
    }
}
