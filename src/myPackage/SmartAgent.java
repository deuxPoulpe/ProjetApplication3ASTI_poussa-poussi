package myPackage;

import java.util.List;

public class SmartAgent extends Agent{

    private int smartness;
    private final int[] weights = {1, 2, 4, 8};
    
    public SmartAgent(char myColor, int smartness) {
        super(myColor);
        this.smartness = smartness;
    }

    public int[] getWeights() {
        return weights;
    }

    public void executeGameRound(Grid grid) {

        // Calcule le meilleur coup à jouer
        GridTree root = new GridTree(this, grid);
        root.generateChildNodes();
        
        GridTree bestMove = evaluateBestMove(root, smartness, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        // Place le jeton sur le plateau
        grid.placeToken(getColor(), bestMove.getPlaceCoordinates());

        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();

        // Si le plateau n'est pas plein, on pousse le jeton choisi dans la direction choisie
        if (grid.isFull()) {
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println("The grid is full. No more tokens can be pushed.");
        } else 
            grid.pushToken(super.getColor(), bestMove.getPushAction().getCoordinates(), bestMove.getPushAction().getDirection());


        if (Settings.getInstance().getDisplayInTerminal())
        grid.display();
       
    }


    public void removeTwoTokens(Grid grid, List<Coordinates> alignment) {
        // TODO Auto-generated method stub
        
    }

    public GridTree evaluateBestMove(GridTree node, int depth, int alpha, int beta, boolean maximizingPlayer) {

        // Si le noeud est une feuille ou si la profondeur est nulle, on retourne le noeud
        if (node.isLeaf() || depth == 0) {
            node.calculateHeuristicValue();
            return node;
        }
    
        // Si c'est le tour du joueur maximisant
        if (maximizingPlayer) {
            GridTree bestChild = null;
            node.generateChildNodes();
    
            // On initialise la meilleure valeur à un très petit nombre
            int maxEval = Integer.MIN_VALUE;
    
            // On parcourt tous les coups possibles
            for (GridTree child : node.getChildren()) {
                // On évalue le noeud enfant
                GridTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, false);
    
                // On calcule la valeur heuristique du noeud enfant
                nodeEval.calculateHeuristicValue();
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
            node.generateChildNodes();
    
            // On initialise la meilleure valeur à un très grand nombre
            int minEval = Integer.MAX_VALUE;
    
            for (GridTree child : node.getChildren()) {
                GridTree nodeEval = evaluateBestMove(child, depth - 1, alpha, beta, true);
    
                // On calcule la valeur heuristique du noeud enfant
                nodeEval.calculateHeuristicValue();
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
