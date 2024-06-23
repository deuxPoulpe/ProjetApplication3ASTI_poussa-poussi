package agentsPackage;

import gamePackage.Action;
import gamePackage.Grid;

public abstract class Agent {

    private char color;
    private int score;


    public abstract Action evaluateAction(Grid grid);
    public abstract void executeAction(Action action);

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
}  
