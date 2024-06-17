package myPackage;
import java.util.Scanner;
import java.util.List;


public class Game {

    private Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private char playerTurn = 'B';
    private int[] score = {0,0};

    // Getters and Setters

    public Grid getGrid() {
        return this.grid;
    }

    public char getPlayerTurn() {
        return this.playerTurn;
    }

    public void setPlayerTurn(char myPlayerTurn) {
        this.playerTurn = myPlayerTurn;
    }

    public void setGrid(Grid myGrid) {
        this.grid = myGrid;
    }

    // Constructor

    public Game(Grid myGrid) {
        this.grid = myGrid;
    }

    // Methods

    public void init() {
        
    }

    public void run() {
        while (true) {
            gameTurn();
            grid.display();
        }
    }
        
    public void gameTurn() {
        System.out.println("Player " + this.playerTurn + " turn");

        // Players choose the token they want to place and push

        do {
            System.out.println("Enter the coordinates of the token you want to place");
        } while (placeToken() == 1);

        grid.display();

        do {
            System.out.println("Enter the coordinates of the token you want to push and the direction you want to push it in (U, D, L, R) put E to not push the token");
        } while (pushToken() == 1);

        // Switch player turn
        if (this.playerTurn == 'B') {
            this.playerTurn = 'Y';
        } else {
            this.playerTurn = 'B';
        }

        // Check if the game is over or if points have been scored
        int gameState = checkScore();

        if (gameState == 0) {
            System.out.println("B wins");
            System.exit(0);
        } else if (gameState == 1) {
            System.out.println("Y wins");
            System.exit(0);
        }
        
        
    }

    public int placeToken() {
        int result = 0;

        int x = scanner.nextInt();
        int y = scanner.nextInt();
        
        Coordinates placeCoords = new Coordinates(x, y);

        try {
            grid.placeToken(playerTurn, placeCoords);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            result = 1;
        }

        return result;
    }

    public int pushToken() {
        int result = 0;

        int x = scanner.nextInt();
        int y = scanner.nextInt();
        char pushDirection = scanner.next().charAt(0);

        if (pushDirection == 'E') {
            return 0;
        }
        
        Coordinates pushCoords = new Coordinates(x, y);
        try {
            grid.pushToken(playerTurn, pushCoords, pushDirection);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            result = 1;
        }

        return result;
    }

    public int checkScore() {
        List<List<List<Coordinates>>> alignmentsOfFive = grid.getAlignmentsOfFive();
        score[0] += alignmentsOfFive.get(0).size();
        score[1] += alignmentsOfFive.get(1).size();

        for (List<Coordinates> alignment : alignmentsOfFive.get(0)) {
            try {
                removeTwoTokens(alignment);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (score[0] == 2) return 0;
        if (score[1] == 2) return 1;
        return -1;

    }

    public void removeTwoTokens(List<Coordinates> alignment) throws Exception {
        String index = "first";
        for (int i = 0; i < 2; i++) {
            
            System.out.println("Enter the coordinates of the " + index + " token you want to remove");
            index = "second";

            int x = scanner.nextInt();
            int y = scanner.nextInt();

            Coordinates removeCoords = new Coordinates(x, y);

            boolean found = false;
            for (Coordinates c : alignment) {
                if (c.equals(removeCoords)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new Exception("The entered coordinates are not in the alignment");
            }

            grid.removeToken(removeCoords);

            grid.display();
        }
    }
}