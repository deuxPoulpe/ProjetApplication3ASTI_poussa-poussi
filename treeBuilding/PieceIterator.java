package treeBuilding;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import game.Board;

public class PieceIterator implements Iterator<int[]> {
    private Board board;
    private int player;
    private int[] next;
    private int currentIndex = 0;
    private List<int[]> cells;

    public PieceIterator(Board board, int player) {
        this.board = board;
        this.player = player;
        this.cells = board.generateShuffledCells();
        moveToNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public int[] next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        int[] piece = next.clone();

        // Move to the next piece for the next call to hasNext
        moveToNext();
        return piece;
    }

    private void moveToNext() {
        // Iterate until we find a valid placement or reach the end of the board
        while (currentIndex < cells.size()) {
            int[] currentCell = cells.get(currentIndex);
            currentIndex++;
            if (board.getPiece(currentCell[0], currentCell[1]) == player) {
                next = currentCell;
                return;
            }
        }
        next = null;
    }
}