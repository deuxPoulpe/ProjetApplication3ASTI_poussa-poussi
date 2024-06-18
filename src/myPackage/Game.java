package myPackage;
import java.util.List;
import java.util.Scanner;


public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private Agent player1;
    private Agent player2;
    private Agent currentPlayer = player1;

    // Getters et Setters

    public Grid getGrid() {
        return this.grid;
    }

    public void setGrid(Grid myGrid) {
        this.grid = myGrid;
    }

    // Constructeur

    public Game(Grid myGrid) {
        this.grid = myGrid;
    }

    // Methods

    public void start() {
        System.out.println("Welcome to the game of Poussa-Poussi!");
        System.out.println("Do you want to play with a friend (1) or against the computer (2), or make computers play against each other (3) ?");
        char choice;

        while ((choice = scanner.next().charAt(0)) != '1' && choice != '2' && choice != '3') {
            System.out.println("Invalid choice. Please enter 1, 2 or 3.");
        }

        grid.display();

        if (choice == '1') {
            this.player1 = new PlayerAgent(grid, 'B');
            this.player2 = new PlayerAgent(grid, 'Y');
        } else if (choice == '2') {
            this.player1 = new PlayerAgent(grid, 'B');
            this.player2 = new RandomAgent(grid, 'Y');
        } else {
            this.player1 = new RandomAgent(grid, 'B');
            this.player2 = new RandomAgent(grid, 'Y');
        }
        currentPlayer = player1;

        run();

    }

    /**
     * Cette méthode exécute le jeu en boucle.
     */
    public void run() {
        
        while (true) {
            Round();

            if (updateScore() == 0) {
                System.out.println("B wins");
                System.exit(0);
            } else if (updateScore() == 1) {
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
        // Phase de placement
        if (grid.isFull()) {
            System.out.println("It's a draw.");
            System.exit(0);
        }
        currentPlayer.placeToken();
        grid.display();

        if (!grid.isFull()) {
            System.out.println("The grid is full. No more tokens can be pushed.");
            currentPlayer.pushToken();
            grid.display();
        }

        // Phase de poussée

        // Vérification du score
        int gameState = updateScore();
        System.out.println("Score: B = " + player1.getScore() + " Y = " + player2.getScore());

        if (gameState == 0) {
            System.out.println("B wins");
            System.exit(0);
        } else if (gameState == 1) {
            System.out.println("Y wins");
            System.exit(0);
        }
    }

    public int updateScore() {
        List<List<List<Coordinates>>> alignmentsOfFive = grid.getAlignmentsOfFive();

        int nbBlueAlignments = alignmentsOfFive.get(0).size();
        int nbYellowAlignments = alignmentsOfFive.get(1).size();

        if (nbBlueAlignments > nbYellowAlignments) 
            player2.incrementScore(nbBlueAlignments - nbYellowAlignments);
        else if (nbYellowAlignments > nbBlueAlignments) 
        player2.incrementScore(nbYellowAlignments - nbBlueAlignments);
            
            int[] order = {0, 1}; // Player 1 first
            if (player2.equals(currentPlayer)) {
                order[0] = 1;
                order[1] = 0;
            }
            
        // Si les deux joueurs ont un score supérieur à 2
        if (player1.getScore() >= 2 && player2.getScore() >=2) {
            // le joueur avec le score le plus élevé gagne
            if (player1.getScore() > player2.getScore())
                return 0;
            else
                return 1;
        } else if (player1.getScore() >= 2) {
            return 0;
        } else if (player2.getScore() >= 2) {
            return 1;
        }

        for (int i : order) {
            for (List<Coordinates> alignment : alignmentsOfFive.get(i)) {
                try {
                    if (i == 0)
                        player1.removeTwoTokens(alignment);
                    else
                        player2.removeTwoTokens(alignment);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return -1;
    }

}