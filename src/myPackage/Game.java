package myPackage;
import java.util.List;
import java.util.Scanner;


public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private char playerTurn = 'B';
    private final int[] score = {0,0};

    // Getters et Setters

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

    // Constructeur

    public Game(Grid myGrid) {
        this.grid = myGrid;
    }

    // Methods

    /**
     * Cette méthode exécute le jeu en boucle.
     */
    public void run() {
        while (true) {
            gameTurn();
        }
    }
    
    /**
     * Cette méthode gère un tour de jeu.
     *
     * Ensuite, elle demande au joueur de placer un jeton sur le plateau.
     * Après cela, elle demande au joueur de pousser un jeton à lui dans une direction spécifiée.
     * Enfin, elle vérifie si le jeu est terminé ou si des points ont été marqués.
     * Ensuite, elle change le tour du joueur.
     */
    public void gameTurn() {
        System.out.println("Player " + this.playerTurn + " turn");

        // Le joueur place son jeton sur le plateau
        do {
            System.out.println("Enter the coordinates of the token you want to place");
        } while (placeToken() == 1);

        // Affiche le plateau de jeu après que le joueur ait placé son jeton
        grid.display();

        // Le joueur pousse son jeton sur le plateau
        do {
            System.out.println("Enter the coordinates of the token you want to push and the direction you want to push it in (U, D, L, R) put E to not push the token");
        } while (pushToken() == 1);

        // Affiche le plateau de jeu après que le joueur ait poussé son jeton
        grid.display();

        // Met à jour le score et vérifie si le jeu est terminé
        int gameState = updateScore();
        System.out.println("Score: B = " + score[0] + " Y = " + score[1]);

        if (gameState == 0) {
            System.out.println("B wins");
            System.exit(0);
        } else if (gameState == 1) {
            System.out.println("Y wins");
            System.exit(0);
        }

        // Change le joueur qui doit jouer
        if (this.playerTurn == 'B') {
            this.playerTurn = 'Y';
        } else {
            this.playerTurn = 'B';
        }
    }

    /**
     * Cette méthode demande au joueur de saisir les coordonnées du jeton qu'il souhaite placer sur le plateau.
     * Si les coordonnées sont invalides, elle affiche un message d'erreur et renvoie 1.
     * @return 0 si le joueur a placé un jeton avec succès, 1 sinon
     */
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

    /**
     * Cette méthode demande au joueur de saisir les coordonnées du jeton qu'il souhaite pousser et la direction dans laquelle il souhaite le pousser.
     * Si le mouvement est invalide, elle affiche un message d'erreur et renvoie 1.
     * @return 0 si le joueur a poussé un jeton avec succès, 1 sinon
     */
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

    /**
     * Cette méthode vérifie si un joueur a marqué des points.
     * Si les deux joueurs ont marqué des points au cours du tour, le score ne change pas
     * Le joueur qui a fait le tour enlève deux jetons en premier si les deux joueurs ont marqué des points
     * @return 0 si le joueur Blue (B) a gagné, 1 si le joueur Yellow (Y) a gagné, -1 sinon
     */
    public int updateScore() {
        List<List<List<Coordinates>>> alignmentsOfFive = grid.getAlignmentsOfFive();

        if (alignmentsOfFive.get(0).isEmpty() || alignmentsOfFive.get(1).isEmpty()) {
            score[0] += alignmentsOfFive.get(0).size();
            score[1] += alignmentsOfFive.get(1).size();
        }

        int[] order = {0, 1}; // Blue player first
        if (playerTurn == 'Y') order = new int[]{1, 0}; // Yellow player first

        for (int i : order) {
            for (List<Coordinates> alignment : alignmentsOfFive.get(i)) {
                try {
                    removeTwoTokens(alignment);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (score[0] == 2) return 0;
        else if (score[1] == 2) return 1;
        return -1;
    }

    /**
     * Cette méthode permet à un joueur de retirer deux jetons d'une ligne de cinq jetons alignés.
     * Elle prend en paramètre une liste de coordonnées représentant l'alignement de cinq jetons.
     * Elle demande au joueur de saisir les coordonnées de deux jetons qu'il souhaite retirer.
     * @param alignment : l'alignement de cinq jetons
     */
    public void removeTwoTokens(List<Coordinates> alignment) {
        String index = "first";
        for (int i = 0; i < 2; i++) {
            

            // Demande au joueur de retirer un jeton de l'alignement
            do {
                System.out.println("Enter the coordinates of the " + index + " token you want to remove");
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
            grid.display();
        }
    }

    /**
     * Cette méthode permet de retirer un jeton d'un alignement de cinq jetons.
     * @param alignment : l'alignement de cinq jetons
     * @return 0 si le jeton a été retiré avec succès, 1 sinon
     * @throws Exception si les coordonnées saisies par le joueur ne sont pas dans l'alignement
     */
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
            grid.removeToken(removeCoords);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return 1;
        }
        alignment.remove(removeCoords);
        return 0;

    }
}