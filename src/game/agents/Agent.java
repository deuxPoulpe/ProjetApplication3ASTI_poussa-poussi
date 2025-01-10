package agents;

public abstract class Agent implements Playable {
    protected int player;

    public Agent(int player) {
        this.player = player;
    }

    public int getId() {
        return player;
    }
    
}
