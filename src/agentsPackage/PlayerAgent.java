package agentsPackage;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Settings;
import java.util.List;
import java.util.Scanner;

public class PlayerAgent extends Agent {

    private Scanner scanner = new Scanner(System.in);

    public PlayerAgent(char myColor) {
        super(myColor);
    }
    
    public void executeGameRound(Grid grid) {

        // Phase de retrait 1
        
        // Pour chaque alignement de 5 jetons du joueur formé par l'adversaire, on retire 2 jetons de l'alignement
        for (List<Coordinates> alignment : grid.getAlignments(super.getColor(), 5)) {
            if (alignment.size() >= 5) {
                removeTwoTokens(grid, alignment);
                rivalGift = rivalGift++;
                
            }
        }

        // Phase de placement
        
        placeToken(grid);

        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();

        // Phase de poussée

        if (grid.isFull()) {
            if (Settings.getInstance().getDisplayInTerminal())
            System.out.println("The grid is full. No more tokens can be pushed.");
        } else 

        pushToken(grid);
        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();

        // Phase de retrait 2
        
        // Pour chaque alignement de 5 jetons formé, on retire 2 jetons de l'alignement
        for (List<Coordinates> alignment : grid.getAlignments(super.getColor(), 5)) {
            if (alignment.size() >= 5) {
                removeTwoTokens(grid, alignment);
                ownPoints = ownPoints++;
            }
        }
    }

    private void placeToken(Grid grid) {

        List<Coordinates> emptyCells = grid.getValidEmptyCoordinates();

        Coordinates placeCoords;
        do {
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println(super.getColor() + " : Enter the coordinates of the token you want to place");


            int x = scanner.nextInt();
            int y = scanner.nextInt();
            
            placeCoords = new Coordinates(x, y);

        } while (!emptyCells.contains(placeCoords));

        grid.placeToken(super.getColor(), placeCoords);
    }

    private void pushToken(Grid grid) {

        int result = 0;

        do {
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println(super.getColor() + " : Enter the coordinates of the token you want to push and the direction you want to push it in (U, D, L, R)");
                if (!Settings.getInstance().getMandatoryPush())
                    System.out.println("Enter E to not push any token");

            int x = scanner.nextInt();
            int y = scanner.nextInt();
            char pushDirection = scanner.next().charAt(0);

            if (!Settings.getInstance().getMandatoryPush() && pushDirection == 'E') {
                return;
            }

            int[] direction = inputToDirection(pushDirection);
            
            Coordinates pushCoords = new Coordinates(x, y);
            try {
                grid.pushToken(super.getColor(), pushCoords, direction);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                result = 1;
            }

        } while (result == 1);
    }

     /**
     * Renvoie les coefficients de déplacement en x et en y pour une direction donnée.
     * 
     * @param direction la direction pour laquelle obtenir les coefficients de déplacement.
     * @throws IllegalArgumentException si la direction n'est pas U, D, R ou L.
     * @return les coefficients de déplacement en x et en y pour la direction donnée.
     */
    private int[] inputToDirection(char direction) {

        if (direction != 'U' && direction != 'D' && direction != 'R' && direction != 'L') {
            throw new IllegalArgumentException("Direction must be U, D, R or L");
        }
        
        int[] coeffs = new int[2];
        switch (direction) {
            case 'U' -> {coeffs[0] = 0; coeffs[1] = -1;}
            case 'D' -> {coeffs[0] = 0; coeffs[1] = 1;}
            case 'R' -> {coeffs[0] = 1; coeffs[1] = 0;}
            case 'L' -> {coeffs[0] = -1; coeffs[1] = 0;}
        }
        return coeffs;
    }
    
    private int removeTokenFromAlignment(Grid grid, List<Coordinates> alignment) throws Exception {

        int x = scanner.nextInt();
        int y = scanner.nextInt();

        Coordinates removeCoords = new Coordinates(x, y);

        // Vérifie si les coordonnées saisies par le joueur sont dans l'alignement
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

        try {
            grid.removeToken(removeCoords);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return 1;
        }
        alignment.remove(removeCoords);
        return 0;

    }

    private void removeTwoTokens(Grid grid, List<Coordinates> alignment) {
        String index = "first";
        for (int i = 0; i < 2; i++) {
            

            // Demande au joueur de retirer un jeton de l'alignement
            do {
                if (Settings.getInstance().getDisplayInTerminal())
                    System.out.println(super.getColor() + " : Enter the coordinates of the " + index + " token you want to remove");
                try {
                    if (removeTokenFromAlignment(grid, alignment) == 0) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (true);

            index = "second";

            // Affiche le plateau de jeu après que le joueur ait retiré un jeton
            if (Settings.getInstance().getDisplayInTerminal())
                grid.display();
        }
    }

}