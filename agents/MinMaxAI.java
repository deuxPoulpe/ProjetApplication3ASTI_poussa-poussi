package agents;

import game.Board;
import actions.Action;

public class MinMaxAI extends Agent {

    private final int[] WEIGHTS = {1, 3, 9, 50};

    public MinMaxAI(int player) {
        super(player);
    }

    public int[] getWeights() {
        return WEIGHTS;
    }
    
    @Override
    public void play(Board board) {
        Action action = findBestAction(board);
        executeAction(board, action);
    }

    private Action findBestAction(Board board) {
        // TODO : Implement this method
        return null;
    }

    private void executeAction(Board board, Action action) {
        // TODO : Implement this method
    }

}
