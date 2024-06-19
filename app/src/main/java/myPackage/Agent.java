package myPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class Agent {

    private Grid grid;
    private char color;
    private int score;

    public abstract void placeToken();
    public abstract AnimationVariables pushToken();
    public abstract void removeTwoTokens(List<Coordinates> alignment);

    public Grid getGrid() {
        return this.grid;
    }

    public char getColor() {
        return this.color;
    }

    public int getScore() {
        return this.score;
    }

    public void incrementScore(int scoreIncremnt) {
        this.score += scoreIncremnt;
    }

    public Agent(Grid myGrid, char myColor) {
        this.grid = myGrid;
        this.color = myColor;
    }

    /**
     * Cette méthode permet trouver les coordonnées des cellules vides du plateau.
     * @return Set<Coordinates> qui contient les coordonnées des cellules vides du plateau.
     */
    public List<Coordinates> getValidEmptyCells () {
        
        // On récupère les coordonnées des cellules non vides
        Set<Coordinates> nonEmptyCells = grid.getGrid().keySet();

        // On initialise un Set qui contiendra les coordonnées des cellules vides
        List<Coordinates> emptyCells = new ArrayList<>();

        // On parcourt le plateau pour trouver les cellules vides
        int gridSize = grid.getSize();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Coordinates coords = new Coordinates(i, j);

                // Si la cellule n'est pas dans le Set des cellules non vides, on l'ajoute au Set des cellules vides
                if (!nonEmptyCells.contains(coords) && (grid.hasNeighbours(coords) || i == 0 || i == grid.getSize() - 1 || j == 0 || j == grid.getSize() - 1)) {
                    emptyCells.add(coords);
                }
            }
        }

        return emptyCells;
    }

    /**
     * Cette méthode permet de récupérer les coordonnées des jetons du joueur.
     * @return List<Coordinates> qui contient les coordonnées des jetons du joueur.
     */
    public List<Coordinates> getOwnTokens () {

        // On initialise une liste qui contiendra les coordonnées des jetons du joueur
        List<Coordinates> ownTokens = new ArrayList<>();

        // On parcourt l'ensemble des jetons du posés sur le plateau
        for (Coordinates coords : grid.getGrid().keySet()) {

            // Si la couleur du jeton est celle du joueur, on l'ajoute à la liste des jetons du joueur
            if (grid.getGrid().get(coords).getColor() == this.color) {
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
    public boolean isValidPushDirection(Coordinates coordinates, int[] direction) {
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

}
