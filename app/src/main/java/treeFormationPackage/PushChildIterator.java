package treeFormationPackage;

import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class PushChildIterator {

    private GridTree node;
    private PushAction currentPushAction = new PushAction(null, null);
    private Iterator<Coordinates> ownTokenCoordsIterator;
    private List<int[]> pushDirections = new ArrayList<>();
    {
        pushDirections.add(new int[]{1, 0});
        pushDirections.add(new int[]{0, 1});
        pushDirections.add(new int[]{1, 1});
        pushDirections.add(new int[]{1, -1});
    }
    private Iterator<int[]> pushDirectionsIterator = pushDirections.iterator();

    public PushChildIterator(GridTree myNode) {
        this.node = myNode;
        this.ownTokenCoordsIterator = new ColorTokenCoordsIterator(myNode.getGrid(), myNode.getAgent().getColor());
        this.currentPushAction = new PushAction(null, null);
        if (ownTokenCoordsIterator.hasNext()) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
        }
    }

    public boolean hasNext() {
        // Si on a encore des jetons à pousser ou des directions de poussée à explorer, on a encore des fils à explorer.
        return node.getGrid().hasValidPush(node.getAgent().getColor());
    }
    
    public GridTree next() {
        if (!hasNext() || !ownTokenCoordsIterator.hasNext() && !pushDirectionsIterator.hasNext()) {
            throw new NoSuchElementException();
        }

        // On retourne un fils pour pousser le jeton courant dans la direction courante
        return createPushChild(node.getGrid());
    }

    private GridTree createPushChildHelper(Grid grid) {

        // On crée un fils pour pousser le jeton courant dans la direction courante
        Grid pushGrid = grid.clone();
        pushGrid.pushToken(node.getAgent().getColor(), currentPushAction.getCoordinates(), currentPushAction.getDirection());
        GridTree pushChild = new GridTree(node, pushGrid, node.getPlaceCoordinates(), currentPushAction);
        return pushChild;
    }

    private GridTree createPushChild(Grid grid) {
        // On instancie les coordonnées de la prochaine poussée
        Coordinates pushCoordinates;

        // Si on n'a pas encore exploré toutes les directions de poussée valides
        if (pushDirectionsIterator.hasNext()) {

            // On pousse le jeton courant dans la prochaine direction
            pushCoordinates = currentPushAction.getCoordinates();
        }
        // Sinon, on passe au jeton suivant
        else if (ownTokenCoordsIterator.hasNext()){
            // On remet à zéro l'itérateur sur les directions de poussée
            pushDirectionsIterator = pushDirections.iterator();
            
            // On récupère le jeton suivant
            pushCoordinates = ownTokenCoordsIterator.next();
        }
        // Sinon, on a exploré toutes les directions de poussée pour tous les jetons
        else {
            throw new NoSuchElementException();
        }

        // Si l'action de poussée est valide
        int[] pushDirection = pushDirectionsIterator.next();
        if (grid.isValidPushDirection(pushCoordinates, pushDirection))
        {
            currentPushAction.setCoordinates(pushCoordinates);
            currentPushAction.setDirection(pushDirection);

            // On retourne un fils pour pousser le jeton courant dans la direction courante
            return createPushChildHelper(grid);
        }

        // Sinon, on réessaie avec la prochaine action de poussée
        return createPushChild(grid);
    }

    private class ColorTokenCoordsIterator implements Iterator<Coordinates> {
        private Grid grid;
        private char color;
        private Iterator<Coordinates> tokenCoordsIterator;
        private Coordinates nextMatchingCoordinate;

        public ColorTokenCoordsIterator(Grid grid, char color) {
            this.grid = grid;
            this.color = color;
            this.tokenCoordsIterator = grid.getHashMap().keySet().iterator();
            findNext(); // Initialise le premier élément correspondant
        }

        private void findNext() {
            while (tokenCoordsIterator.hasNext()) {
                Coordinates current = tokenCoordsIterator.next();
                if (grid.getToken(current).getColor() == color) {
                    nextMatchingCoordinate = current;
                    return;
                }
            }
            nextMatchingCoordinate = null; // Aucun autre élément correspondant
        }

        @Override
        public boolean hasNext() {
            return nextMatchingCoordinate != null;
        }

        @Override
        public Coordinates next() {
            if (nextMatchingCoordinate == null) {
                throw new NoSuchElementException();
            }
            Coordinates currentMatchingCoordinate = nextMatchingCoordinate;
            findNext(); // Prépare le prochain élément correspondant pour le prochain appel
            return currentMatchingCoordinate;
        }
    }
}
