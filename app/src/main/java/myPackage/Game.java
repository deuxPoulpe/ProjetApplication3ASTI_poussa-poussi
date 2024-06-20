package myPackage;
import java.util.List;
import java.util.Scanner;


public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private final int[] score = {0,0};
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

    public char getColorOfCurrentPlayer() {
        return currentPlayer.getColor();
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

    public int[] getScore() {
        return score;
    }

    // Constructeur

    public Game(Grid myGrid) {
        this.grid = myGrid;
    }

    // Methods



    public void start(char choice) {

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

    public void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    public void Round() {
        // Phase de placement
        currentPlayer.placeToken();
        grid.display();

        // Phase de poussée
        currentPlayer.pushToken();
        grid.display();

        // Vérification du score
        int gameState = updateScore();
        System.out.println("Score: B = " + score[0] + " Y = " + score[1]);

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
            score[0] += nbBlueAlignments - nbYellowAlignments;
        else if (nbYellowAlignments > nbBlueAlignments) 
            score[1] += nbYellowAlignments - nbBlueAlignments;
            
        if (score[0] == 2) return 0;
        else if (score[1] == 2) return 1;
        
        int[] order = {0, 1}; // Player 1 first
        if (player2.equals(currentPlayer)) {
            order[0] = 1;
            order[1] = 0;
        }
        
        //for (int i : order) {
        //    for (List<Coordinates> alignment : alignmentsOfFive.get(i)) {
        //        try {
        //            if (i == 0)
        //                player1.removeTwoTokens(alignment);
        //           else
        //                player2.removeTwoTokens(alignment);

        //        } catch (Exception e) {
        //            System.out.println(e.getMessage());
        //        }
        //    }
        //}
        return -1;
    }

}