package gamePackage;
import java.util.Scanner;

import agentsPackage.Agent;
import agentsPackage.MinMaxAgent;
import agentsPackage.PlayerAgent;


public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private Agent player1;
    private Agent player2;
    private Agent currentPlayer = player1;

    private int[] scores = {0, 0};

    private int orangeAiDifficulty;
    private int blueAiDifficulty;

    public Game(Grid myGrid) {
        this.grid = myGrid;
    }

    public Game(Grid myGrid, int blueAiDifficulty, int orangeAiDifficulty) {
        this.grid = myGrid;
        this.blueAiDifficulty = blueAiDifficulty;
        this.orangeAiDifficulty = orangeAiDifficulty;
    }

    public Agent getCurrentPlayer() {
        return currentPlayer;
    }

    public int[] getScores() {
        return scores;
    }

    public char getColorOfCurrentPlayer() {
        return currentPlayer.getColor();
    }

    public void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    public Agent getPlayer1() {
        return player1;
    }
    public Agent getPlayer2() {
        return player2;
    }

    public void start(char choice) {
        // Initialisation des joueurs
        if (Settings.getInstance().getDisplayInTerminal()) {
            System.out.println("Welcome to the game of Poussa-Poussi!");
            System.out.println("Do you want to play with a friend (1) or against the computer (2), or make computers play against each other (3) ?");
        }

//        char choice;
//
//        while ((choice = scanner.next().charAt(0)) != '1' && choice != '2' && choice != '3') {
//            System.out.println("Invalid choice. Please enter 1, 2 or 3.");
//        }

        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();

        if (choice == '1') {
            this.player1 = new PlayerAgent('B', scores);
            this.player2 = new PlayerAgent('Y', scores);
        } else if (choice == '2') {
            this.player1 = new PlayerAgent('B', scores);
            this.player2 = new MinMaxAgent('Y', orangeAiDifficulty, scores);
        } else {
            this.player1 = new MinMaxAgent('B', blueAiDifficulty, scores);
            this.player2 = new MinMaxAgent('Y', orangeAiDifficulty, scores);
        }
        currentPlayer = player1;
    }

    public void run() {
        
        while (true) {
            executeRound();

            if (checkScore() == 0) {
                System.out.println("B wins");
                System.exit(0);
            } else if (checkScore() == 1) {
                System.out.println("Y wins");
                System.exit(0);
            }

            if (currentPlayer == player1) {
                currentPlayer = player2;
            } else {
                currentPlayer = player1;
            }
        }
    }

    public void executeRound() {
        // Avant placement du jeton
        if (grid.isFull()) {
            System.out.println("It's a draw.");
            System.exit(0);
        }
        // Placement et poussée du jeton
        Action action = currentPlayer.evaluateAction(grid);
        currentPlayer.executeAction(action);

        // Vérification du score
        int gameState = checkScore();

        if (gameState == 0) {
            System.out.println("B wins");
            System.exit(0);
        } else if (gameState == 1) {
            System.out.println("Y wins");
            System.exit(0);
        }
    }
 
    public int checkScore() {

        return -1;
    }

}