package iteratorsPackage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import gamePackage.CoordinateSetGridPair;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.GridTree;

public class PlaceChildIterator implements Iterator<GridTree> {
    
    private GridTree node;
    private EmptyCoordinateIterator emptyCoordIterator;
    private RemovGridIterator removGridIterator;
    private Coordinates currentPlaceCoordinates;

    public PlaceChildIterator(GridTree myNode) {
        this.node = myNode;
        Grid grid = myNode.getGrid();
        this.emptyCoordIterator = new EmptyCoordinateIterator(grid);
        this.removGridIterator = new RemovGridIterator(grid, grid.getAlignments(myNode.getAgent().getColor(), 5));

    }

    @Override
    public boolean hasNext() {

        // Si on a encore des coordonnées vides ou des combinaisons de jetons à retirer, on a encore des fils à explorer.
        if (emptyCoordIterator.hasNext() || removGridIterator.hasNext()) {
            return true;
        }
        return false;
    }

    @Override
    public GridTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // On instancie un fils pour placer un jeton à la coordonnée actuelle.
        GridTree placeChild = null;
        
        // On récupère la prochaine coordonnée vide.
        currentPlaceCoordinates = emptyCoordIterator.next();

        // Si on a une combinaison de jetons à retirer, on crée un fils pour placer un jeton à la coordonnée actuelle et retirer les jetons de la combinaison.
        if (removGridIterator.hasNext()) {
            CoordinateSetGridPair removCombinationGridPair = removGridIterator.next();
            placeChild = createPlaceChild(removCombinationGridPair.getGrid());
            placeChild.getRemovCoordinates().set(1, removCombinationGridPair.getCoordinates());
            
        }
        // Sinon, on crée un fils pour placer un jeton à la coordonnée actuelle.
        else {
            placeChild = createPlaceChild(node.getGrid());
        }

        return placeChild;
    }

    /**
     * Crée un fils du noeud courant pour placer un jeton à une coordonnée donnée.
     * @param grid la grille sur laquelle placer le jeton.
     * @param coords les coordonnées où placer le jeton.
     * @return le fils qui contient la grille obtenue après avoir placé le jeton, et les coordonnées où le jeton a été placé.
     */
    private GridTree createPlaceChild(Grid grid) {

        // On inialise un clone du plateau avec le jeton placé
        Grid placeGrid = grid.clone();
        placeGrid.placeToken(node.getAgent().getColor(), currentPlaceCoordinates);
        return new GridTree(node, grid, currentPlaceCoordinates, null);
    }

    public class EmptyCoordinateIterator implements Iterator<Coordinates> {
        private Grid grid;
        Coordinates currentCoordinates;

        public EmptyCoordinateIterator(Grid grid) {
            this.grid = grid;
        }

        @Override
        public boolean hasNext() {
            return currentCoordinates.getX() < grid.getSize() && currentCoordinates.getY() < grid.getSize();
        }

        @Override
        public Coordinates next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // On récupère les coordonnées des cellules non vides
            Set<Coordinates> nonEmptyCells = grid.getHashMap().keySet();

            // Teste si les coordonnées actuelles sont dans les cellules non vides et si elles ont des voisins
            boolean gridContainsCurrentCoordinates;
            boolean hasNeighbours;

            do {
                // Si on est à la fin d'une ligne, on passe à la ligne suivante
                if (currentCoordinates.getX() == grid.getSize() - 1) {
                    currentCoordinates.setX(0);
                    currentCoordinates.setY(currentCoordinates.getY() + 1);

                // Sinon, on passe à la colonne suivante
                } else {
                    currentCoordinates.setX(currentCoordinates.getX() + 1);
                }

                // On effectue les tests
                gridContainsCurrentCoordinates = nonEmptyCells.contains(currentCoordinates);
                hasNeighbours = grid.hasNeighbours(currentCoordinates);

            // Répéter jusqu'à ce que les coordonnées actuelles soient dans les cellules vides et aient des voisins
            } while (gridContainsCurrentCoordinates || !hasNeighbours);

            return currentCoordinates;
        }
    }

}