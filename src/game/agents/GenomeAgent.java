package agents;

import core.Board;
import ai.NEAT.Genome;
import actions.Action;

public class GenomeAgent extends Agent {

    private Genome genome;

    public GenomeAgent(int player, Genome genome) {
        super(player);
        this.genome = genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    @Override
    public void play(Board board) {
        // Wait 1500ms 
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Action action = evaluateAction(board);
        executeAction(board, action);
    }

    private Action evaluateAction(Board board) {
        // TODO : Implement this method
        return null;
    }

    private void executeAction(Board board, Action action) {
        if (action.getStartRemoval() != null) {
            board.remove(action.getStartRemoval());
            System.out.println("Player " + player + " removed " + action.getStartRemoval());
        }

        if (action.getPlacement() != null) {
            board.place(action.getPlacement(), player);;
            System.out.println("Player " + player + " placed " + action.getPlacement());
        }

        if (action.getPush() != null) {
            board.push(action.getPush());
            System.out.println("Player " + player + " pushed " + action.getPush());
        }

        if (action.getEndRemoval() != null) {
            board.remove(action.getEndRemoval());
            System.out.println("Player " + player + " removed " + action.getEndRemoval());
        }
    }
    
}
