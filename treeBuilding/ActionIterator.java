package treeBuilding;

import java.util.Iterator;
import java.util.NoSuchElementException;

import game.Board;
import game.Settings;
import actions.Removal;
import actions.Placement;
import actions.Push;
import actions.Action;

public class ActionIterator implements Iterator<Action>{

    private Board board;
    private int player;
    private PlacementIterator placementIterator;
    private PushIterator pushIterator;
    private RemovalIterator startRemovalIterator;
    private RemovalIterator endRemovalIterator;
    
    public ActionIterator(Board board, int player) {
        this.board = board;

        // On initialise l'itérateur de retrait en début de tour
        this.startRemovalIterator = new RemovalIterator(board, player);

        // Si on n'a pas de retrait en début de tour, on initialise l'itérateur de placement
        if (!startRemovalIterator.hasNext()) {
            this.placementIterator = new PlacementIterator(board);
        }
    }

    @Override
    public boolean hasNext() {
        return startRemovalIterator.hasNext() || placementIterator.hasNext() || pushIterator.hasNext() || endRemovalIterator.hasNext();
    }

    @Override
    public Action next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        boolean actionPending = true;
        Action action = new Action();
        Board newBoard = board.clone();

        while (actionPending) {

            // Si on a un prochain retrait en fin de tour
            if (endRemovalIterator != null && endRemovalIterator.hasNext()) {

                // On explore le prochain retrait en fin de tour
                Removal endRemoval = endRemovalIterator.next();
                newBoard.remove(endRemoval);
                action.setEndRemoval(endRemoval);

                // On sort de la boucle
                actionPending = false;
            }

            // Si on a une prochaine poussée
            else if (pushIterator != null && pushIterator.hasNext()) {

                // On explore la prochaine poussée
                Push push = pushIterator.next();
                newBoard.push(push);
                action.setPush(push);

                // On réinitialise l'itérateur de retrait en fin de tour sur la prochaine grille de poussée
                endRemovalIterator = new RemovalIterator(newBoard, player);

                // Si on n'a pas de retrait en fin de tour, on sort de la boucle
                if (!endRemovalIterator.hasNext()) {
                    actionPending = false;
                }
            }

            // Si on a un prochain placement
            else if (placementIterator != null && placementIterator.hasNext()) {

                // On explore le prochain placement
                Placement placement = placementIterator.next();
                newBoard.place(placement, player);
                action.setPlacement(placement);
                
                // On réinitialise l'itérateur de poussée sur le prochain placement
                pushIterator = new PushIterator(newBoard, player);
                
                // Si on a le droit de ne pas pousser
                if (!Settings.getInstance().getMandatoryPush()) {
                    // On réinitialise l'itérateur de retrait en fin de tour sur la prochaine grille de placement
                    endRemovalIterator = new RemovalIterator(newBoard, player);
                    if (!endRemovalIterator.hasNext() && !Settings.getInstance().getMandatoryPush()) {
                        actionPending = false;
                    }
                }
            }

            // Si on a un prochain retrait en début de tour
            else if (startRemovalIterator != null && startRemovalIterator.hasNext()) {

                // On explore le prochain retrait en début de tour
                Removal startRemoval = startRemovalIterator.next();
                newBoard.remove(startRemoval);
                action.setStartRemoval(startRemoval);
 
                // On réinitialise l'itérateur de placement sur le prochain retrait en début de tour
                placementIterator = new PlacementIterator(newBoard);

                // Si on n'a pas de placement, on sort de la boucle
                if (!placementIterator.hasNext()) {
                    actionPending = false;
                }
            }
        }

        return action;
    }
}