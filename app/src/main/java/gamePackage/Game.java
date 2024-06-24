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

    private int agent1Smartness;
    private int agent2Smartness;

    public Game(Grid myGrid, int agent1Smartness, int agent2Smartness) {
        this.grid = myGrid;
        this.agent1Smartness = agent1Smartness;
        this.agent2Smartness = agent2Smartness;
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

    public Agent getCurrentPlayer() {
        return currentPlayer;
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

        //char choice;

        //while ((choice = scanner.next().charAt(0)) != '1' && choice != '2' && choice != '3') {
        //    System.out.println("Invalid choice. Please enter 1, 2 or 3.");
        //}

        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();

        if (choice == '1') {
            this.player1 = new PlayerAgent('B');
            this.player2 = new PlayerAgent('Y');
        } else if (choice == '2') {
            this.player1 = new PlayerAgent('B');
            this.player2 = new MinMaxAgent('Y', agent2Smartness);
        } else {
            this.player1 = new MinMaxAgent('B', agent1Smartness);
            this.player2 = new MinMaxAgent('Y', agent2Smartness);
        }
        currentPlayer = player1;
    }

    public void run() {
        
        while (true) {
            Round();

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

    public void Round() {
        // Avant placement du jeton
        if (grid.isFull()) {
            System.out.println("It's a draw.");
            System.exit(0);
        }
        // Placement et poussée du jeton
        currentPlayer.executeGameRound(grid);

        // Vérification du score
        int gameState = checkScore();
        if (Settings.getInstance().getDisplayInTerminal())
            System.out.println("Score: B = " + player1.getScore() + " Y = " + player2.getScore());

        if (gameState == 0) {
            System.out.println("B wins");
            System.exit(0);
        } else if (gameState == 1) {
            System.out.println("Y wins");
            System.exit(0);
        }
    }
 
    public int checkScore() {
        // TODO: Implement this method

        return -1;
    }
}