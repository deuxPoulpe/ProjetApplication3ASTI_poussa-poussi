package agentsPackage;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import gamePackage.Settings;
import treeFormationPackage.GridTree;

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
        GridTree root = new GridTree(this, grid);
        GridTree bestMove = evaluateBestMove(root, smartness, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        // Phase de retrait 1

        rivalGift =  bestMove.getRemovCoordinates().get(0).size() / 2;


        // Pour chaque alignement de 5 jetons du joueur formé par l'adversaire, on retire 2 jetons de l'alignement
        for (Coordinates removCoords : bestMove.getRemovCoordinates().get(0)) {
            grid.removeToken(removCoords);
        }

        // Phase de placement

        // Place le jeton sur le plateau
        grid.placeToken(getColor(), bestMove.getPlaceCoordinates());
        
        if (Settings.getInstance().getDisplayInTerminal())
        grid.display();
        
        // Phase de poussée

        PushAction pushAction = bestMove.getPushAction();
        // Si le plateau n'est pas plein, on pousse le jeton choisi dans la direction choisie
        if (grid.isFull()){
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println("The grid is full. No more tokens can be pushed.");
        } else if (pushAction == null) {
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println(super.getColor() + " : does not push any token");
        } else 
            grid.pushToken(super.getColor(), bestMove.getPushAction().getCoordinates(), bestMove.getPushAction().getDirection());

        // Phase de retrait 2

        ownPoints = bestMove.getRemovCoordinates().get(1).size() / 2;

        // Pour chaque alignement de 5 jetons formé, on retire 2 jetons de l'alignement
        for (Coordinates removCoords : bestMove.getRemovCoordinates().get(1)) {
            grid.removeToken(removCoords);
        }

        if (Settings.getInstance().getDisplayInTerminal())
        grid.display();
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
    public GridTree evaluateBestMove(GridTree node, int depth, int alpha, int beta, boolean maximizingPlayer) {

        // Si le noeud est une feuille ou si la profondeur est nulle, on retourne le noeud
        if (depth == 0) {
            node.calculateHeuristicValue();
            return node;
        }

        // Si c'est le tour du joueur maximisant
        if (maximizingPlayer) {
            GridTree bestChild = null;

            // On initialise la meilleure valeur à un très petit nombre
            int maxEval = Integer.MIN_VALUE;

            // On parcourt tous les coups possibles
            GridTree child;
            while ((child = node.getNextChild()) != null) {

                // On calcule la profondeur du noeud enfant
                child.setDepth(node.getDepth() + 1);

                // On évalue le noeud enfant
                GridTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, false);

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
            GridTree bestChild = null;

            // On initialise la meilleure valeur à un très grand nombre
            int minEval = Integer.MAX_VALUE;

            GridTree child;
            while ((child = node.getNextChild()) != null) {
                GridTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, true);

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