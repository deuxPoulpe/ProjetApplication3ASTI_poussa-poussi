package treeFormationPackage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.EmptyCoordIterator;
import gamePackage.Grid;

public class PlaceIterator implements Iterator<ActionTree> {
    
    private ActionTree node;
    private EmptyCoordIterator emptyCoordIterator;
    private Coordinates currentPlaceCoordinates;

    public PlaceIterator(ActionTree myNode) {
        this.node = myNode;
        Grid grid = myNode.getGrid();
        this.emptyCoordIterator = new EmptyCoordIterator(grid);
    }

    @Override
    public boolean hasNext() {

        // Si on a encore des coordonnées vides ou des combinaisons de jetons à retirer, on a encore des fils à explorer.
        if (emptyCoordIterator.hasNext()) {
            return true;
        }
        return false;
    }

    @Override
    public ActionTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // On retourne un fils pour placer un jeton à la coordonnée actuelle.
        return createPlaceChild(node.getGrid());
    }

    /**
     * Crée un fils du noeud courant pour placer un jeton à une coordonnée donnée.
     * @param grid la grille sur laquelle placer le jeton.
     * @param coords les coordonnées où placer le jeton.
     * @return le fils qui contient la grille obtenue après avoir placé le jeton, et les coordonnées où le jeton a été placé.
     */
    private ActionTree createPlaceChild(Grid grid) {

        // On récupère la prochaine coordonnée vide.
        currentPlaceCoordinates = emptyCoordIterator.next();

        // On inialise un clone du plateau avec le jeton placé
        Grid placeGrid = grid.clone();
        placeGrid.placeToken(node.getAgent().getColor(), currentPlaceCoordinates);

        return new ActionTree(node, placeGrid, currentPlaceCoordinates, null);
    }
}