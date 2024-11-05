package game;

import agents.Playable;
import agents.Player;

public class GameLoop {

    private Playable player1;
    private Playable player2;

    public GameLoop() {
        // Create the players
        this.player1 = new Player(1);
        this.player2 = new Player(2);
    }
    
    public static void main(String[] args) {
        // Create a new board
        Board board = new Board(8);

        // Create a new game loop
        GameLoop gameLoop = new GameLoop();

        // Start the game loop
        gameLoop.startGameLoop(board);
    }

    public void startGameLoop(Board board) {
        // Game loop
        while (true) {
            // Update the game state
            updateGameState(board);

            // Render the game state
            renderGameState(board);

            // Check if the game is over
            if (isGameOver(board)) {
                break;
            }
        }
    }

    public void updateGameState(Board board) {
        player1.play(board);
        player2.play(board);
    }

    public void renderGameState(Board board) {
        // Render the game state here
    }

    public boolean isGameOver(Board board) {
        // Check if the game is over
        return false;
    }
    
}
