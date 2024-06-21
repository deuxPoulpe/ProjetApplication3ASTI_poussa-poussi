package treeFormationPackage;

import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class PushChildIterator implements Iterator<ActionTree> {

    private ActionTree node;
    private PushAction currentPushAction = new PushAction(null, null);
    private ColorTokenCoordsIterator ownTokenCoordsIterator;
    private List<int[]> pushDirections = new ArrayList<>();
    {
        pushDirections.add(new int[]{1, 0});
        pushDirections.add(new int[]{0, 1});
        pushDirections.add(new int[]{-1, 0});
        pushDirections.add(new int[]{0, -1});
    }
    private Iterator<int[]> directionsIterator;

    public PushChildIterator(ActionTree myNode) {
        this.node = myNode;
        this.ownTokenCoordsIterator = new ColorTokenCoordsIterator(myNode.getGrid(), myNode.getAgent().getColor());
        this.directionsIterator = pushDirections.iterator();
    }

    private boolean hasNextHelper(Iterator<Coordinates> ownTokenCoordsIteratorClone, Iterator<int[]> directionsIteratorClone, PushAction currentPushActionClone) {
        // Si on a encore des directions de poussée à explorer
        if (directionsIterator.hasNext()) {
            currentPushAction.setDirection(directionsIterator.next());
            // Si l'action de poussée est valide, on a encore des fils à explorer
            if (node.getGrid().isValidPushAction(currentPushAction, node.getAgent().getColor())) {
                return true;
            }

            // Sinon, on passe à la prochaine direction
            return hasNext();
        
        } 
        // Si on a encore des jetons à explorer
        if (ownTokenCoordsIterator.hasNext()) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
            directionsIterator = pushDirections.iterator();

            // On itère sur les directions de poussée
            return hasNext();
        }

        // Sinon, on n'a plus de fils à explorer
        return false;
    }

    @Override
    public boolean hasNext() {
        // On clone les itérateurs pour ne pas les modifier
        ColorTokenCoordsIterator ownTokenCoordsIteratorClone = ownTokenCoordsIterator.clone();
        Iterator<int[]> directionsIteratorClone = pushDirections.iterator();

        // On se place sur la direction courante
        int[] currentDirection = null;
        if (directionsIteratorClone.hasNext()) {
            currentDirection = directionsIteratorClone.next();
        }

        while (directionsIteratorClone.hasNext() && !Arrays.equals(currentDirection, currentPushAction.getDirection())) {
            currentDirection = directionsIteratorClone.next();
        }

        PushAction currentPushActionClone = null;
        if (currentPushAction.getCoordinates() != null) {
            Coordinates currentPushCoords = new Coordinates(currentPushAction.getCoordinates().getX(), currentPushAction.getCoordinates().getY());
            currentPushActionClone = new PushAction(currentPushCoords, currentDirection);
        }

        return hasNextHelper(ownTokenCoordsIteratorClone, directionsIteratorClone, currentPushActionClone);
    }

    private ActionTree createPushChildHelper(Grid grid) {

        // On clone le plateau courant
        Grid pushGrid = grid.clone();

        // On effectue la poussée du jeton courant dans la direction courante sur le plateau cloné
        pushGrid.pushToken(currentPushAction, node.getAgent().getColor());

        // On crée un fils pour pousser le jeton courant dans la direction courantes
        ActionTree pushChild = new ActionTree(node, pushGrid, node.getPlaceCoordinates(), currentPushAction);
        return pushChild;
    }

    private ActionTree createPushChild(Grid grid) {
        ActionTree pushChild = null;

        // Si on n'a pas encore exploré toutes les directions de poussée valides
        if (directionsIterator.hasNext() && currentPushAction.getCoordinates() != null) {

            // On met à jour la direction courante
            currentPushAction.setDirection(directionsIterator.next());

            // Si l'action de poussée est valide, on retourne un fils pour pousser le jeton courant dans la direction courante
            if (grid.isValidPushAction(currentPushAction, node.getAgent().getColor()))
                pushChild = createPushChildHelper(grid);
            
            else // Sinon, on passe à la prochaine direction
                pushChild = createPushChild(grid);
        }
        // Sinon, on passe au jeton suivant
        else if (ownTokenCoordsIterator.hasNext()){
            // On remet à zéro l'itérateur sur les directions de poussée
            directionsIterator = pushDirections.iterator();
            
            // On met à jour les coordonnées du jeton courant
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());

            // On appelle récursivement la fonction pour créer un fils pour pousser le jeton suivant
            pushChild = createPushChild(grid);
        }

        return pushChild;
    }
    
    @Override
    public ActionTree next() {
        if (!hasNext() || !ownTokenCoordsIterator.hasNext() && !directionsIterator.hasNext()) {
            throw new NoSuchElementException();
        }

        // On retourne un fils pour pousser le jeton courant dans la direction courante
        return createPushChild(node.getGrid());
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

        public ColorTokenCoordsIterator clone() {

            ColorTokenCoordsIterator clone = new ColorTokenCoordsIterator(grid.clone(), color);
            clone.nextMatchingCoordinate = nextMatchingCoordinate == null ? null : new Coordinates(nextMatchingCoordinate.getX(), nextMatchingCoordinate.getY());
            return clone;
        }
    }
}
