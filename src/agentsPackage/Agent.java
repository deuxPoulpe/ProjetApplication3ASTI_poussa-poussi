package agentsPackage;

import gamePackage.Coordinates;
import gamePackage.Grid;
import java.util.ArrayList;
import java.util.List;

public abstract class Agent {

    private char color;
    private int score;
    public int rivalGift = 0;
    public int ownPoints = 0;

    public int getrivalGift() {
        return rivalGift;
    }

    public int getownPoints() {
        return ownPoints;
    }

    public int setrivalGift(int rivalGift) {
        return this.rivalGift = rivalGift;
    }

    public int setownPoints(int ownPoints) {
        return this.ownPoints = ownPoints;
    }

    public void resetPoints() {
        this.rivalGift = 0;
        this.ownPoints = 0;
    }

    public abstract void executeGameRound(Grid grid);

    public char getColor() {
        return this.color;
    }

    public int getScore() {
        return this.score;
    }

    public int setScore(int score) {
        return this.score = score;
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
            if (grid.isValidPushDirection(coordinates, direction)) {
                validDirections.add(direction);
            }
        }
        return validDirections;
    }
}  
