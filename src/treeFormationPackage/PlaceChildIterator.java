package treeFormationPackage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.Grid;

public class PlaceChildIterator implements Iterator<GridTree> {
    
    private GridTree node;
    private EmptyCoordinateIterator emptyCoordIterator;
    private Coordinates currentPlaceCoordinates;

    public PlaceChildIterator(GridTree myNode) {
        this.node = myNode;
        Grid grid = myNode.getGrid();
        this.emptyCoordIterator = new EmptyCoordinateIterator(grid);
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
    public GridTree next() {
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
    private GridTree createPlaceChild(Grid grid) {

        // On récupère la prochaine coordonnée vide.
        currentPlaceCoordinates = emptyCoordIterator.next();

        // On inialise un clone du plateau avec le jeton placé
        Grid placeGrid = grid.clone();
        placeGrid.placeToken(node.getAgent().getColor(), currentPlaceCoordinates);

        return new GridTree(node, placeGrid, currentPlaceCoordinates, null);
    }

    private class EmptyCoordinateIterator implements Iterator<Coordinates> {
        private Grid grid;
        Coordinates currentCoordinates;

        public EmptyCoordinateIterator(Grid grid) {
            this.grid = grid;
            this.currentCoordinates = getNextEmptyCell(new Coordinates(0, 0));
        }

        @Override
        public boolean hasNext() {
            return currentCoordinates != null;
        }

        @Override
        public Coordinates next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Coordinates next = currentCoordinates;
            currentCoordinates = getNextEmptyCell(new Coordinates(currentCoordinates.getX() + 1, currentCoordinates.getY()));

            return next;
        }

        private Coordinates getNextEmptyCell(Coordinates start) {
            for (int y = start.getY(); y < grid.getSize(); y++) {
                for (int x = (y == start.getY() ? start.getX() : 0); x < grid.getSize(); x++) {
                    Coordinates coordinates = new Coordinates(x, y);

                    boolean hasNeighbours = grid.hasNeighbours(coordinates);
                    boolean isOnBorder = coordinates.getX() == 0 || coordinates.getY() == 0 || coordinates.getX() == grid.getSize() - 1 || coordinates.getY() == grid.getSize() - 1;
                    boolean isNotOccupied = !grid.getHashMap().containsKey(coordinates);

                    // Si la case est vide et a des voisins ou est sur le bord, on la retourne
                    if (isNotOccupied && (hasNeighbours || isOnBorder)) {
                        return coordinates;
                    }
                }
            }

            return null;
        }
    }

}