package myPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void placeToken(Grid grid) {
        // TODO Auto-generated method stub
        
        
    }
    
    public void pushToken(Grid grid) {
        // TODO Auto-generated method stub
        
    }

    public void executeRound(Grid grid) {
        // TODO Auto-generated method stub
        
    }

    public void removeTwoTokens(Grid grid, List<Coordinates> alignment) {
        // TODO Auto-generated method stub
        
    }

    

    

    

    public GridTree minMaxAlphaBeta(GridTree node, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (node.isLeaf() || depth == 0) {
            node.calculateHeuristicValue();
            return node;
        }
        
        if (maximizingPlayer) {
            GridTree bestChild = null;
            node.generateChildNodes();
            for (GridTree child : node.getChildren()) {
                GridTree nodeEval = minMaxAlphaBeta(child, depth - 1, alpha, beta, false);
                nodeEval.calculateHeuristicValue();
                if (bestChild == null || nodeEval.getHeuristicValue() < bestChild.getHeuristicValue()) {
                    bestChild = nodeEval;
                }
            }
        }    
    }

    public void alphaPruning(GridTree node, int alpha) {

    }

    public void betaPruning(GridTree node, int beta) {

    }

    
}
