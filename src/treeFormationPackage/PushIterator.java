package treeFormationPackage;

import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class PushIterator implements Iterator<ActionTree> {

    private ActionTree node;
    private PushAction currentPushAction = new PushAction(null, null);
    private ColorTokenCoordsIterator ownTokenCoordsIterator;
    private final List<int[]> pushDirections = new ArrayList<>();
    {
        pushDirections.add(new int[]{1, 0}); // Droite
        pushDirections.add(new int[]{0, 1}); // Bas
        pushDirections.add(new int[]{-1, 0}); // Gauche
        pushDirections.add(new int[]{0, -1}); // Haut
    }
    private Iterator<int[]> directionsIterator = pushDirections.iterator(); // Initialise l'itérateur des directions

    public PushIterator(ActionTree myNode) {
        this.node = myNode;
        this.ownTokenCoordsIterator = new ColorTokenCoordsIterator(myNode.getAction().getGrid(), myNode.getAgent().getColor());
        if (ownTokenCoordsIterator.hasNext()) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
        }
        // Réinitialise l'itérateur des directions pour le premier jeton
        this.directionsIterator = pushDirections.iterator();
    }

    @Override
    public boolean hasNext() {

        // Si la direction est valide, on retourne vrai
        if (currentPushAction.getDirection() != null && node.getAction().getGrid().isPushValid(currentPushAction, node.getAgent().getColor())) {
            return true;

        // Sinon, si on a une prochaine direction, on vérifie si elle est valide
        } else if (directionsIterator.hasNext()) {
            currentPushAction.setDirection(directionsIterator.next());
            return hasNext();

        // Sinon, si on a un prochain jeton à pousser, on vérifie si on peut le pousser
        } else if (ownTokenCoordsIterator.hasNext()) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
            directionsIterator = pushDirections.iterator(); // Réinitialise l'itérateur des directions pour le nouveau jeton
            currentPushAction.setDirection(null);
            return hasNext();

        // Sinon, on retourne faux
        } else {
            return false;
        }
    }

    @Override
    public ActionTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // On crée un nouvel enfant avec la copie de la grille actuelle
        Coordinates placementClone = node.getAction().getPlacement() != null ? node.getAction().getPlacement().clone() : null;
        ActionTree child = new ActionTree(node, node.getAction().getGrid().clone(), placementClone, currentPushAction.clone());

        // On effectue l'action de poussée sur la grille du nouvel enfant
        child.getAction().getGrid().pushToken(currentPushAction, node.getAgent().getColor());

        // Si on a une prochaine direction, on se place dessus
        if (directionsIterator.hasNext()) {
            currentPushAction.setDirection(directionsIterator.next());
        }
        // Sinon, si on a un prochain jeton à pousser, on se place dessus
        else if (ownTokenCoordsIterator.hasNext()) {
            currentPushAction.setCoordinates(ownTokenCoordsIterator.next());
            directionsIterator = pushDirections.iterator(); // Réinitialise l'itérateur des directions pour le nouveau jeton
            currentPushAction.setDirection(null);
        }
        // Sinon, on se place sur une direction nulle
        else {
            currentPushAction.setDirection(null);
        }

        child.incrementDepth();

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
    }
}