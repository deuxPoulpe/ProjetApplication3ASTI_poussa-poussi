package core;

import agents.Playable;
import agents.Player;

public class GameLoop {

    private Playable player1;
    private Playable player2;
    private Board board;

    public GameLoop() {
        // Create the players
        this.player1 = new Player(1);
        this.player2 = new Player(2);
        this.board = new Board();
    }

    public GameLoop(Playable player1, Playable player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Board();
    }

    public GameLoop(Board board, Playable player1, Playable player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Playable getPlayer1() {
        return player1;
    }

    public void setPlayer1(Playable player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Playable player2) {
        this.player2 = player2;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    
    public int run() {
        // Game loop
        while (true) {
            // Update the game state
            updateGameState();

            // Render the game state
            System.out.println(board);

            // Check if the game is over
            int winner = getWinner();
            if (winner != 0) {
                board.reset();
                return winner;
            }
        }
    }

    public void reset() {
        board.reset();
    }

    private void updateGameState() {
        player1.play(board);
        player2.play(board);
        System.out.println(board);
    }

    private int getWinner() {
        // TODO: Check if the game is over
        return 0;
    }
}
