package gamePackage;
import agentsPackage.Agent;
import agentsPackage.MinMaxAgent;
import agentsPackage.PlayerAgent;
import java.util.Scanner;


public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private Agent player1;
    private Agent player2;
    private Agent currentPlayer = player1;

    public Game(Grid myGrid) {
        this.grid = myGrid;
    }

    public void start() {
        // Initialisation des joueurs
        if (Settings.getInstance().getDisplayInTerminal()) {
            System.out.println("Welcome to the game of Poussa-Poussi!");
            System.out.println("Do you want to play with a friend (1) or against the computer (2), or make computers play against each other (3) ?");
        }

        char choice;

        while ((choice = scanner.next().charAt(0)) != '1' && choice != '2' && choice != '3') {
            System.out.println("Invalid choice. Please enter 1, 2 or 3.");
        }

        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();

        if (choice == '1') {
            this.player1 = new PlayerAgent('B');
            this.player2 = new PlayerAgent('Y');
        } else if (choice == '2') {
            this.player1 = new PlayerAgent('B');
            this.player2 = new MinMaxAgent('Y', 2);
        } else {
            this.player1 = new MinMaxAgent('B', 1);
            this.player2 = new MinMaxAgent('Y', 1);
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

            currentPlayer = (currentPlayer == player1) ? player2 : player1;
        }
    }

    public void Round() {
        if (grid.isFull()) {
            System.out.println("It's a draw.");
            System.exit(0);
        }

        currentPlayer.executeGameRound(grid);

        // Après l'exécution du tour, enregistrer les points marqués par le joueur précédent
        if (currentPlayer == player1) {
            // Mettre à jour le score pour le joueur 1
            int ownPoints = player1.getownPoints();
            player1.setScore(player1.getScore() + ownPoints - player2.getrivalGift());
        } else {
            // Mettre à jour le score pour le joueur 2
            int ownPoints = player2.getownPoints();
            player2.setScore(player2.getScore() + ownPoints - player1.getrivalGift());
        }

        player1.resetPoints();
        player2.resetPoints();

        int gameState = checkScore();
        if (Settings.getInstance().getDisplayInTerminal()) {
            System.out.println("Score: B = " + player1.getScore() + " Y = " + player2.getScore());
        }

        if (gameState == 0) {
            System.out.println("It's a draw.");
            System.exit(0);
        } else if (gameState == 1) {
            System.out.println("B wins");
            System.exit(0);
        } else if (gameState == 2) {
            System.out.println("Y wins");
            System.exit(0);
        } else if (gameState == 3) {
            System.out.println("The game is ongoing");
        }
    }
 
    public int checkScore() {
        if (grid.isFull())
        {
            return 0;
        }
        else
        {
            if (player1.getScore() > player2.getScore() && player1.getScore() >= 2) {
                return 1;
            } else if (player1.getScore() < player2.getScore() && player2.getScore() >= 2) {
                return 2;
            } else 
                return 3;
        }
    }
}