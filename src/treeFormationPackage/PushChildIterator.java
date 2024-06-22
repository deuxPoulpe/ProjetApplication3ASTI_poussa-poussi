package treeFormationPackage;

import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class PushChildIterator implements Iterator<ActionTree> {

    private ActionTree node;
    private PushAction currentPushAction = new PushAction(null, null);
    private ColorTokenCoordsIterator ownTokenCoordsIterator;
    private List<int[]> pushDirections = new ArrayList<>();
    {
        pushDirections.add(new int[]{1, 0}); // Droite
        pushDirections.add(new int[]{0, 1}); // Bas
        pushDirections.add(new int[]{-1, 0}); // Gauche
        pushDirections.add(new int[]{0, -1}); // Haut
    }
    private Iterator<int[]> directionsIterator;

    public PushChildIterator(ActionTree myNode) {
        this.node = myNode;
        this.ownTokenCoordsIterator = new ColorTokenCoordsIterator(myNode.getGrid(), myNode.getAgent().getColor());
    }

    @Override
    public boolean hasNext() {

        // Retourne vrai si l'action de poussée actuelle est valide
        if (currentPushAction.getDirection() != null && node.getGrid().isValidPushAction(currentPushAction, node.getAgent().getColor())) {
            return true;
        }

        // S'il reste des directions à explorer
        if (directionsIterator != null && directionsIterator.hasNext()) {

            // On itère sur la prochaine direction
            currentPushAction.setDirection(directionsIterator.next());

            // On appelle récursivement la méthode pour vérifier s'il est possible de pousser dans la direction actuelle
            return hasNext();
        }

        // S'il n'y a plus de directions à explorer, mais qu'il reste des jetons de la couleur de l'agent à explorer
        if (ownTokenCoordsIterator.hasNext()) {

            // On passe au prochain jeton de la couleur de l'agent
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());

            // On réinitialise l'itérateur de directions
            directionsIterator = pushDirections.iterator();

            // On appelle récursivement la méthode pour vérifier s'il est possible de pousser à partir du prochain jeton
            return hasNext();
        }

        // Si les coordonnées du jeton à pousser n'ont pas été initialisées, on les initialise
        if (currentPushAction.getCoordinates() == null) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
            directionsIterator = pushDirections.iterator();
        }

        // S'il n'y a plus de jetons de la couleur de l'agent
        return false;
    }

    @Override
    public ActionTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // On génère un fils pour pousser dans la direction actuelle
        Grid gridClone = node.getGrid().clone();
        gridClone.pushToken(currentPushAction, node.getAgent().getColor());
        ActionTree child = new ActionTree(node, gridClone, node.getPlaceCoordinates(), currentPushAction);

        // On passe à la prochaine direction si possible
        if (directionsIterator != null && directionsIterator.hasNext()) {
            currentPushAction.setDirection(directionsIterator.next());
        }

        // Sinon, on passe au prochain jeton de la couleur de l'agent
        else if (ownTokenCoordsIterator.hasNext()) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
            directionsIterator = pushDirections.iterator();
        }

        return child;
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
