package agentsPackage;

import java.util.List;
import java.util.Scanner;
import java.util.HashSet;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import gamePackage.Settings;
import gamePackage.Action;

public class PlayerAgent extends Agent {

    private Scanner scanner = new Scanner(System.in);

    public PlayerAgent(char myColor) {
        super(myColor);
    }

    public PlayerAgent(char myColor, int[] scores) {
        super(myColor, scores);
    }
    
    @Override
    public void executeAction(Action action) {

        // Retire les jetons de l'alignement de 5 jetons de l'adversaire
        for (Coordinates removCoords : action.getStartRemove()) {
            action.getGrid().removeToken(removCoords);
        }

        // Place le jeton sur le plateau
        if (action.getPlacement() != null) {
            action.getGrid().placeToken(getColor(), action.getPlacement());
        }

        // Pousse le jeton si une poussée est possible
        if (action.getPush() != null) {
            action.getGrid().pushToken(action.getPush(), getColor());
        }

        // Retire les jetons de l'alignement de 5 jetons du joueur
        for (Coordinates removCoords : action.getEndRemove()) {
            action.getGrid().removeToken(removCoords);
        }

    }

    @Override
    public Action evaluateAction(Grid grid) {

        Action action = new Action(new HashSet<>(), null, null, new HashSet<>(), grid);
        Grid gridCopy = grid.clone();

        // Pour chaque alignement de 5 jetons du joueur formé par l'adversaire, on retire 2 jetons de l'alignement
        List<List<Coordinates>> alignments = grid.getAlignments(getColor(), 5);
        grid.clearAlignments(alignments);
        for (int i = 0; i < alignments.size(); i++) {
            List<Coordinates> alignment = alignments.get(i);
            for (int j = 0; j < 2; j++) {
                Coordinates startRemCoords = inputRemove(alignment);
                action.getStartRemove().add(startRemCoords);
                gridCopy.removeToken(startRemCoords);

                if (Settings.getInstance().getDisplayInTerminal()) {
                    gridCopy.display();
                }
            }
        }

        // Place le jeton sur le plateau si la grille n'est pas pleine
        if (!grid.isFull()) {
            Coordinates placement = inputPlacement(grid);
            action.setPlacement(placement);
            gridCopy.placeToken(getColor(), placement);

            if (Settings.getInstance().getDisplayInTerminal()) {
                gridCopy.display();
            }
        }

        // Détermine si une poussée est possible
        int[][] directions = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
        boolean hasValidPush = false;
        for (int i = 0; i < gridCopy.getSize(); i++) {
            for (int j = 0; j < gridCopy.getSize(); j++) {
                Coordinates coords = new Coordinates(i, j);
                // Vérifie si la case contient un jeton avant de tenter de pousser
                if (gridCopy.getHashMap().containsKey(coords)) {
                    for (int[] direction : directions) {
                        PushAction pushAction = new PushAction(coords, direction);
                        if (gridCopy.isPushValid(pushAction, getColor())) {
                            hasValidPush = true;
                            break;
                        }
                    }
                    if (hasValidPush) {
                        break; // Sort de la boucle si une poussée valide est trouvée
                    }
                }
            }
            if (hasValidPush) {
                break; // Sort de la boucle externe si une poussée valide est trouvée
            }
        }

        // Si une poussée est possible, on demande au joueur de pousser un jeton
        if (hasValidPush) {
            PushAction pushAction = inputPush(gridCopy);
            action.setPush(pushAction);
            gridCopy.pushToken(pushAction, getColor());

            if (Settings.getInstance().getDisplayInTerminal()) {
                gridCopy.display();
            }
        }

        // Pour chaque alignement de 5 jetons du joueur formé par le joueur, on retire 2 jetons de l'alignement
        alignments = grid.getAlignments(getColor(), 5);
        grid.clearAlignments(alignments);
        for (int i = 0; i < alignments.size(); i++) {
            List<Coordinates> alignment = alignments.get(i);
            for (int j = 0; j < 2; j++) {
                Coordinates endRemCoords = inputRemove(alignment);
                action.getEndRemove().add(endRemCoords);
                gridCopy.removeToken(endRemCoords);

                if (Settings.getInstance().getDisplayInTerminal()) {
                    gridCopy.display();
                }
            }
        }   
        
        return action;
    }

    private Coordinates inputPlacement(Grid grid) {

        Coordinates placeCoords;
        do {
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println(super.getColor() + " : Enter the coordinates of the token you want to place");


            int x = scanner.nextInt();
            int y = scanner.nextInt();
            
            placeCoords = new Coordinates(x, y);

        } while (!grid.isPlaceValid(placeCoords));

        return placeCoords;
    }

    private PushAction inputPush(Grid grid) {

        PushAction pushAction = null;

        do {
            if (Settings.getInstance().getDisplayInTerminal()) {
                System.out.println(super.getColor() + " : Enter the coordinates of the token you want to push and the direction you want to push it in (U, D, L, R)");
                if (!Settings.getInstance().getMandatoryPush()) {
                    System.out.println("Enter E to not push any token");
                }
            }

            int x = scanner.nextInt();
            int y = scanner.nextInt();
            char pushDirection = scanner.next().charAt(0);

            if (!Settings.getInstance().getMandatoryPush() && pushDirection == 'E') {
                return null;
            }

            int[] direction = inputToDirection(pushDirection);
            Coordinates pushCoords = new Coordinates(x, y);

            pushAction = new PushAction(pushCoords, direction);

        } while (!grid.isPushValid(pushAction, getColor()));

        return pushAction;
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

//        switch (direction) {
//            case 'U' -> {coeffs[0] = 0; coeffs[1] = -1;}
//            case 'D' -> {coeffs[0] = 0; coeffs[1] = 1;}
//            case 'R' -> {coeffs[0] = 1; coeffs[1] = 0;}
//            case 'L' -> {coeffs[0] = -1; coeffs[1] = 0;}
//        }
        return coeffs;
    }
    
    private Coordinates inputRemove(List<Coordinates> alignment) {
        boolean found = false;
        Coordinates removeCoords = null;

        while (!found) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();

            removeCoords = new Coordinates(x, y);

            // Vérifie si les coordonnées saisies par le joueur sont dans l'alignement
            for (Coordinates c : alignment) {
                if (c.equals(removeCoords)) {
                    found = true;
                    alignment.remove(removeCoords);
                    break;
                }
            }
        }

        return removeCoords;
    }
}