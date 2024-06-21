package agentsPackage;

import java.util.ArrayList;
import java.util.List;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;

public abstract class Agent {

    private char color;
    private int score;

    public abstract void executeGameRound(Grid grid);

    public char getColor() {
        return this.color;
    }

    public int getScore() {
        return this.score;
    }

    public void incrementScore(int scoreIncremnt) {
        this.score += scoreIncremnt;
    }

    public Agent(char myColor) {
        this.color = myColor;
    }

    public List<int[]> getValidPushDirections(Grid grid, Coordinates coordinates) {
        // On initialise toutes les directions possibles dans une liste
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        List<int[]> validDirections = new ArrayList<>();
        for (int[] direction : directions) {
            PushAction pushAction = new PushAction(coordinates, direction);
            try {
                grid.getMovedTokens(pushAction, grid.getTokensToMove(pushAction, color));
            }
            catch (Exception e) {
                continue;
            }
            validDirections.add(direction);
        }
        return validDirections;
    }
}  
