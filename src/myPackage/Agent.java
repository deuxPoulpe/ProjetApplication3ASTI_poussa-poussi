package myPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Agent {

    private char color;
    private int score;

    public abstract void executeGameRound(Grid grid);

    public char getColor() {
        return this.color;
    }

    public int getScore() {
        return this.score;
    }

    public void incrementScore(int scoreIncremnt) {
        this.score += scoreIncremnt;
    }

    public Agent(char myColor) {
        this.color = myColor;
    }

    /**
     * Cette méthode permet de récupérer les coordonnées des jetons du joueur.
     * @return List<Coordinates> qui contient les coordonnées des jetons du joueur.
     */
    public List<Coordinates> getOwnTokensCoords (Grid grid) {

        // On initialise une liste qui contiendra les coordonnées des jetons du joueur
        List<Coordinates> ownTokens = new ArrayList<>();

        // On parcourt l'ensemble des jetons du posés sur le plateau
        for (Coordinates coords : grid.getHashMap().keySet()) {

            // Si la couleur du jeton est celle du joueur, on l'ajoute à la liste des jetons du joueur
            if (grid.getToken(coords).getColor() == this.color) {
                ownTokens.add(coords);
            }
        }

        return ownTokens;
    }

    /**
     * Cette méthode permet vérifier si une direction est valide pour pousser un jeton.
     * @param coordinates les coordonnées du jeton à pousser
     * @param direction la direction aléatoire
     * @return int[] qui contient les coefficients de la direction aléatoire valide
     */
    public boolean isValidPushDirection(Grid grid, Coordinates coordinates, int[] direction) {
        try {
            int coeffX = direction[0];
            int coeffY = direction[1];

            // On récupère les jetons à déplacer
            HashMap<Coordinates, Token> tokensToMove = grid.getTokensToMove(coordinates, coeffX, coeffY);

            // On récupère les coordonnées du dernier jeton à déplacer
            Coordinates lastToken = new Coordinates(coordinates.getX() + (tokensToMove.size() - 1) * coeffX, coordinates.getY() + (tokensToMove.size() - 1) * coeffY);

            // On récupère le nombre de cellules vides dans la direction donnée
            int nbEmptyCells = grid.getNbEmptyCellsInDirection(lastToken, coeffX, coeffY);

            // On simule le déplacement des jetons
            HashMap<Coordinates, Token> tokensMoveEnd = new HashMap<>();
            for (Coordinates c : tokensToMove.keySet()) {
                tokensMoveEnd.put(new Coordinates(c.getX() + nbEmptyCells * coeffX, c.getY() + nbEmptyCells * coeffY), tokensToMove.get(c));
            }
            
            if (Settings.getInstance().getAllowPushBack()) {
                // Si la direction fait pousser les jetons dans la même position qu'au tour d'avant, on la retire de la liste des directions valides
                if (grid.getTokensMoveStart().equals(tokensMoveEnd)) {
                    return false;
                }
            }
        }
        catch (IllegalArgumentException e) {
            // Si la direction fait pousser contre un bord du plateau, on la retire de la liste des directions valides
            return false;
        }
        return true;
    }

    public List<int[]> getValidPushDirections(Grid grid, Coordinates coordinates) {
        // On initialise toutes les directions possibles dans une liste
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        List<int[]> validDirections = new ArrayList<>();
        for (int[] direction : directions) {
            if (isValidPushDirection(grid, coordinates, direction)) {
                validDirections.add(direction);
            }
        }
        return validDirections;
    }
}  
