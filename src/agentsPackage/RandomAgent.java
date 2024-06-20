package agentsPackage;

import java.util.List;

import myPackage.Coordinates;
import myPackage.Grid;
import myPackage.Settings;

public class RandomAgent extends Agent {

    public RandomAgent(char myColor) {
        super(myColor);
    }

    /**
     * Cette méthode permet de placer un jeton aléatoire sur une cellule vide.
     * @return void
     */
    public void placeToken(Grid grid) {
        List<Coordinates> emptyCells = grid.getValidEmptyCoordinates();

        // On prend une cellule vide aléatoire
        int random = (int) (Math.random() * emptyCells.size());

        if (Settings.getInstance().getDisplayInTerminal())
            System.out.println("RandomAgent: " + super.getColor() + " places token at " + emptyCells.get(random));
        grid.placeToken(super.getColor(), emptyCells.get(random));
    }

    /**
     * Cette méthode permet de pousser un jeton aléatoire dans une direction aléatoire.
     * @return void
     */
    public void pushToken(Grid grid) {

        // On récupère les coordonnées des jetons du joueur
        List<Coordinates> ownTokens = grid.getColorTokenCoordinates(getColor());

        Coordinates randomTokenCoords;
        int[] randomDirection;
        do {
            // On prend un jeton aléatoire
            int random = (int) (Math.random() * ownTokens.size());
            randomTokenCoords = ownTokens.get(random);
            ownTokens.remove(random);

            // On prend une direction aléatoire valide
            randomDirection = getRandomDirection(grid, randomTokenCoords);
        }
        while (randomDirection == null);
        
        if (Settings.getInstance().getDisplayInTerminal()) {
            System.out.printf("RandomAgent: %c pushes token at %s ", super.getColor(), randomTokenCoords);
            System.out.printf("in direction %d, %d\n", randomDirection[0], randomDirection[1]);
        }

        grid.pushToken(super.getColor(), randomTokenCoords, randomDirection);
    }


    public void executeGameRound(Grid grid) {
        placeToken(grid);
        if (Settings.getInstance().getDisplayInTerminal())
            grid.display();
        if (grid.isFull()) {
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println("The grid is full. No more tokens can be pushed.");
        } else 
            pushToken(grid);
        if (Settings.getInstance().getDisplayInTerminal())
        grid.display();
       
    }

    /**
     * Cette méthode permet de retirer deux jetons aléatoires d'une liste de jetons alignés.
     * @param alignment la liste des coordonnées des jetons alignés
     * @return void
     */
    public void removeTwoTokens(Grid grid, List<Coordinates> alignment) {
        // Randomly remove two tokens
        int random = (int) (Math.random() * alignment.size());
        Coordinates firstToken = alignment.get(random);
        alignment.remove(random);
        random = (int) (Math.random() * alignment.size());
        Coordinates secondToken = alignment.get(random);
        
        grid.removeToken(firstToken);
        grid.removeToken(secondToken);
    }

    /**
     * Cette méthode permet de trouver une direction aléatoire valide pour pousser un jeton.
     * @param coords les coordonnées du jeton à pousser
     * @return int[] un tableau de deux entiers qui représente la direction aléatoire valide
     */
    private int[] getRandomDirection(Grid grid, Coordinates coords) {
        // On initialise toutes les directions possibles dans une liste
        List<int[]> validDirections = super.getValidPushDirections(grid, coords);

        while (validDirections.size() > 0) {
        
            // On prend une direction aléatoire
            int random = (int) (Math.random() * validDirections.size());
            int[] randomDirection = validDirections.get(random);

            // Si la direction est valide, on la retourne
            if (super.isValidPushDirection(grid, coords, randomDirection)) {
                return randomDirection;
            }

            // Sinon, on la retire de la liste des directions possibles
            validDirections.remove(random);
        }

        // If no valid direction is found, return null
        return null;
    }
}
