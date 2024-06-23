import agentsPackage.MinMaxAgent;
import gamePackage.*;
import treeFormationPackage.*;

public class TestH {

    public static void main (String[] args) {

    Grid grille = new Grid();
    
    grille.placeToken('Y', new Coordinates(0, 0));
    grille.placeToken('Y', new Coordinates(0, 1));
    grille.placeToken('Y', new Coordinates(0, 2));
    grille.placeToken('Y', new Coordinates(0, 3));
    grille.placeToken('Y', new Coordinates(0, 4));
    grille.placeToken('Y', new Coordinates(0, 5));

    grille.placeToken('Y', new Coordinates(6, 0));
    grille.placeToken('Y', new Coordinates(6, 1));
    grille.placeToken('Y', new Coordinates(6, 2));

    // grille.placeToken('B', new Coordinates(1, 0));
    // grille.placeToken('B', new Coordinates(1, 1));
    // grille.placeToken('B', new Coordinates(1, 2));
    
    
    grille.display();

    MinMaxAgent agent = new MinMaxAgent('Y', 1);
    ActionTree root = new ActionTree(agent, grille);
    root.calculateHeuristicValue();
    int value = root.getHeuristicValue();
    System.out.println(value);

    }
}
