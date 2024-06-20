package iteratorsPackage;

import java.util.NoSuchElementException;

import agentsPackage.MinMaxAgent;
import myPackage.CoordinateSetGridPair;
import myPackage.Coordinates;
import myPackage.Grid;
import myPackage.GridTree;
import myPackage.PushAction;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class PushChildIterator {

    private GridTree node;
    private RemovGridIterator removGridIterator = null;
    private PushAction currentPushAction;
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
        this.ownTokenCoordsIterator = myNode.getGrid().getColorTokenCoordinates(myNode.getAgent().getColor()).iterator();
        this.currentPushAction = new PushAction(null, null);
    }

    public boolean hasNext() {

        // TODO: Inexact, on peut ne pas avoir de jetons à pousser sans que la grille soit pleine.
        return !node.getGrid().isFull();
    }
    
    public GridTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // Si aucun allignement de 5 n'a été formé par l'itération précédente
        if (removGridIterator == null || !removGridIterator.hasNext()) {
            GridTree pushChild =  createPushChild(node.getGrid());
            Grid pushGrid = pushChild.getGrid();

            // On crée un itérateur pour retirer deux jetons par alignement formé suite à la poussée
            removGridIterator = new RemovGridIterator(pushGrid, pushGrid.getAlignments(node.getAgent().getColor(), 5));
            if (removGridIterator.hasNext()) {

                // On retire la première combinaison de jetons
                CoordinateSetGridPair remCoordinateSetGridPair = removGridIterator.next();
                pushChild.getRemovCoordinates().set(1, remCoordinateSetGridPair.getCoordinates());
                pushChild.setGrid(remCoordinateSetGridPair.getGrid());
                return pushChild;
            }
        }
        // Si un allignement de 5 a été formé par l'itération précédente, On retire la prochaine combinaison de jetons

        CoordinateSetGridPair remCoordinateSetGridPair = removGridIterator.next();
        GridTree pushChild = createPushChild(remCoordinateSetGridPair.getGrid());
        pushChild.getRemovCoordinates().set(1, remCoordinateSetGridPair.getCoordinates());

        return pushChild;
    }

    private GridTree createPushChildHelper(Grid grid) {
        MinMaxAgent agent = node.getAgent();

        // On crée un fils pour pousser le jeton courant dans la direction courante
        GridTree pushChild = new GridTree(agent, grid.clone());
        pushChild.getGrid().pushToken(agent.getColor(), currentPushAction.getCoordinates(), currentPushAction.getDirection());
        return pushChild;
    }

    private GridTree createPushChild(Grid grid) {
        // On instancie les coordonnées de la prochaine poussée
        Coordinates pushCoordinates;

        // Si on n'a pas encore exploré toutes les directions de poussée
        if (pushDirectionsIterator.hasNext()) {

            // On pousse le jeton courant dans la prochaine direction
            pushCoordinates = currentPushAction.getCoordinates();
        }
        // Sinon, on passe au jeton suivant
        else {
            // On remet à zéro l'itérateur sur les directions de poussée
            pushDirectionsIterator = pushDirections.iterator();
            
            // On récupère le jeton suivant
            pushCoordinates = ownTokenCoordsIterator.next();
        }
        currentPushAction.setCoordinates(pushCoordinates);
        currentPushAction.setDirection(pushDirectionsIterator.next());

        // On retourne un fils pour pousser le jeton courant dans la direction courante
        return createPushChildHelper(grid);
    }
}
