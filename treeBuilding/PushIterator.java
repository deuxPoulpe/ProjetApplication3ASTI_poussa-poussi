package treeBuilding;

import java.util.NoSuchElementException;

import game.Board;
import actions.Push;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PushIterator implements Iterator<Push> {

    private Board board;
    private Push currentPush = new Push();
    private PieceIterator pieceIterator;
    private final List<int[]> pushDirections = new ArrayList<>();
    {
        pushDirections.add(new int[]{1, 0}); // Right
        pushDirections.add(new int[]{0, 1}); // Down
        pushDirections.add(new int[]{-1, 0}); // Left
        pushDirections.add(new int[]{0, -1}); // Up
    }
    private Iterator<int[]> directionsIterator;
    {
        Collections.shuffle(pushDirections);
        directionsIterator = pushDirections.iterator();
    } 

    public PushIterator(Board board, int player) {
        this.board = board;
        this.pieceIterator = new PieceIterator(board, player);

        // Move to the next piece to push
        if (pieceIterator.hasNext()) {
            currentPush.setCoordinates(pieceIterator.next());
        }
       
    }

    @Override
    public boolean hasNext() {

        // If the current push is valid, return true
        if (currentPush.getDirection() != null && board.findPushDistance(currentPush) > 0) {
            return true;
        }

        // If we have a next direction, check if we can push the piece in that direction
        if (directionsIterator.hasNext()) {
            currentPush.setDirection(directionsIterator.next());

            // Call hasNext recursively to check if the push is valid
            return hasNext();
        }

        // If we have a next cell, check if we can push the piece in that cell
        if (pieceIterator.hasNext()) {
            currentPush.setCoordinates(pieceIterator.next());

            // Reset the directions iterator for the new cell
            Collections.shuffle(pushDirections);
            directionsIterator = pushDirections.iterator();

            // Reset current push the direction
            currentPush.setDirection(null);

            // Call hasNext recursively to check if the push is valid
            return hasNext();
        }

        // Return false if there are no more pushes to make
        return false;
    }

    @Override
    public Push next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // Clone the current push
        Push push = currentPush.clone();

        // If we have a next direction, we set it
        if (directionsIterator.hasNext()) {
            currentPush.setDirection(directionsIterator.next());
        }
        // Else, if we have a next cell, we set it
        else if (pieceIterator.hasNext()) {
            currentPush.setCoordinates(pieceIterator.next());
            directionsIterator = pushDirections.iterator(); // Réinitialise l'itérateur des directions pour le nouveau jeton
            currentPush.setDirection(null);
        }
        // Else, we set the direction to null
        else {
            currentPush.setDirection(null);
        }

        // Return the cloned push
        return push;
    }

}