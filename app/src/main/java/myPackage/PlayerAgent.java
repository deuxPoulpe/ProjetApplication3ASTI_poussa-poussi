package myPackage;

import java.util.List;
import java.util.Scanner;

public class PlayerAgent extends Agent {

    private Scanner scanner = new Scanner(System.in);

    public PlayerAgent(Grid myGrid, char myColor) {
        super(myGrid, myColor);
    }

   
    public void placeToken() {

        int result = 0;
        do {
            System.out.println(super.getColor() + " : Enter the coordinates of the token you want to place");


            int x = scanner.nextInt();
            int y = scanner.nextInt();
            
            Coordinates placeCoords = new Coordinates(x, y);

            try {
                super.getGrid().placeToken(super.getColor(), placeCoords);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                result = 1;
            }
        } while (result == 1);
    }

    public void pushToken() {

        int result = 0;

        do {
            System.out.println(super.getColor() + " : Enter the coordinates of the token you want to push and the direction you want to push it in (U, D, L, R) put E to not push the token");

            int x = scanner.nextInt();
            int y = scanner.nextInt();
            char pushDirection = scanner.next().charAt(0);

            if (pushDirection == 'E') {
                return;
            }

            int[] direction = inputToDirection(pushDirection);
            
            Coordinates pushCoords = new Coordinates(x, y);
            try {
                super.getGrid().pushToken(super.getColor(), pushCoords, direction);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                result = 1;
            }

        } while (result == 1);
    }

    public void removeTwoTokens(List<Coordinates> alignment) {
        String index = "first";
        for (int i = 0; i < 2; i++) {
            

            // Demande au joueur de retirer un jeton de l'alignement
            do {
                System.out.println(super.getGrid().getToken(alignment.get(0)).getColor() + " : Enter the coordinates of the " + index + " token you want to remove");
                try {
                    if (removeTokenFromAlignment(alignment) == 0) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } while (true);

            index = "second";

            // Affiche le plateau de jeu après que le joueur ait retiré un jeton
            super.getGrid().display();
        }
    }

     /**
     * Renvoie les coefficients de déplacement en x et en y pour une direction donnée.
     * 
     * @param direction la direction pour laquelle obtenir les coefficients de déplacement.
     * @throws IllegalArgumentException si la direction n'est pas U, D, R ou L.
     * @return les coefficients de déplacement en x et en y pour la direction donnée.
     */
    public int[] inputToDirection(char direction) {

        if (direction != 'U' && direction != 'D' && direction != 'R' && direction != 'L') {
            throw new IllegalArgumentException("Direction must be U, D, R or L");
        }
        
        int[] coeffs = new int[2];
        switch (direction) {
            case 'U':
                coeffs[0] = 0;
                coeffs[1] = -1;
                break;
            case 'D':
                coeffs[0] = 0;
                coeffs[1] = 1;
                break;
            case 'R':
                coeffs[0] = 1;
                coeffs[1] = 0;
                break;
            case 'L':
                coeffs[0] = -1;
                coeffs[1] = 0;
                break;
        }
        //switch (direction) {
        //    case 'U' -> {coeffs[0] = 0; coeffs[1] = -1;}
        //    case 'D' -> {coeffs[0] = 0; coeffs[1] = 1;}
        //    case 'R' -> {coeffs[0] = 1; coeffs[1] = 0;}
        //    case 'L' -> {coeffs[0] = -1; coeffs[1] = 0;}
        //}
        return coeffs;
    }
    
    public int removeTokenFromAlignment(List<Coordinates> alignment) throws Exception {

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
            super.getGrid().removeToken(removeCoords);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return 1;
        }
        alignment.remove(removeCoords);
        return 0;

    }

}