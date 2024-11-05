package treeBuilding;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import actions.Placement;
import game.Board;


public class PlacementIterator implements Iterator<Placement> {
    private Board board;
    private int currentIndex;
    private List<int[]> cells;

    public PlacementIterator(Board board) {
        this.board = board;
        cells = board.generateShuffledCells();
        this.currentIndex = 0;
        moveToNext();
    }

    @Override
    public boolean hasNext() {
        return currentIndex < cells.size();
    }

    @Override
    public Placement next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // Create a new placement object and move to the next valid placement
        Placement placement = new Placement(cells.get(currentIndex));
        currentIndex++;
        moveToNext();

        return placement;
    }

    private void moveToNext() {
        // Iterate until we find a valid placement or reach the end of the board
        while (currentIndex < cells.size() && !board.isValidPlacement(cells.get(currentIndex)[0], cells.get(currentIndex)[1])) {
            currentIndex++;
        }
    }
}