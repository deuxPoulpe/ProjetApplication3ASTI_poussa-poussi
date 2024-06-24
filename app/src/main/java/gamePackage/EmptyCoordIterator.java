package gamePackage;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class EmptyCoordIterator implements Iterator<Coordinates> {
    private Grid grid;
    Coordinates currentCoordinates;

    public EmptyCoordIterator(Grid grid) {
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

                // Si la case est vide et a des voisins ou est sur le bord, on la retourne
                if (grid.isPlaceValid(coordinates)) {
                    return coordinates;
                }
            }
        }

        return null;
    }
}