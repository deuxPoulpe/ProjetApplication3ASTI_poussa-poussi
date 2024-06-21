package treeFormationPackage;

import java.util.Set;
import java.util.Iterator;

import gamePackage.Coordinates;
import gamePackage.Grid;

public class ChildIterator implements Iterator<ActionTree>{

    private ActionTree node;
    private PlaceChildIterator placeChildIterator;
    private PushChildIterator pushChildIterator;
    private RemovGridIterator removGridIterator;
    private CoordinateSetGridPair currentRemCoordSetGridPair;
    private ActionTree currentPlaceChild;
    private ActionTree currentPushChild;
    private ActionTree currentChild;

    public ChildIterator(ActionTree myNode) {
        this.node = myNode;
        this.placeChildIterator = new PlaceChildIterator(myNode);
        Grid myGrid = myNode.getGrid();
        this.removGridIterator = new RemovGridIterator(myGrid, myGrid.getAlignments(myNode.getAgent().getColor(), 5));
        this.currentPlaceChild = placeChildIterator.next();
        this.pushChildIterator = new PushChildIterator(currentPlaceChild);
    }

    @Override
    public boolean hasNext() {

        // S'il reste un retrait à explorer, on a encore des fils à explorer
        if (removGridIterator.hasNext()) {
            return true;
        }

        // S'il reste une poussée à explorer, on a encore des fils à explorer
        if (pushChildIterator.hasNext()) {
            currentPushChild = pushChildIterator.next();
            return true;
        }
        
        // S'il reste un placement à explorer, on a encore des fils à explorer
        if (placeChildIterator.hasNext()) {
            currentPlaceChild = placeChildIterator.next();
            pushChildIterator = new PushChildIterator(currentPlaceChild);
            return true;
        }

        // S'il n'y a plus de fils à explorer
        return false;
    }

    @Override
    public ActionTree next() {
       
        // Si on a un retrait à explorer
        if (removGridIterator.hasNext()) {
            currentRemCoordSetGridPair = removGridIterator.next();

            // On récupère les coordonnées à retirer et la grille obtenue après le retrait
            Set<Coordinates> remCoords = currentRemCoordSetGridPair.getCoordinates();
            Grid remGrid = currentRemCoordSetGridPair.getGrid();

            // Si on est à la racine de l'arbre, on met les jetons à retirer au premier index
            if (node.getDepth() == 0) {
                node.getRemovCoordinates().add(0, remCoords);
                node.setGrid(remGrid);
                return next();
            }
            // Sinon, on met les jetons à retirer au deuxième index et on explore un placement dans la grille obtenue
            node.getRemovCoordinates().add(1, remCoords);
            return createChild(remGrid);
        }
        // Si on n'a pas de retrait à explorer, on explore une poussée ou un placement dans la grille actuelle
        return createChild(node.getGrid());
    }
    
    private ActionTree createChild(Grid grid) {
        ActionTree child = null;

        // Si on a une poussée à explorer, on retourne le fils correspondant
        if (currentPushChild != null) {
            currentPushChild = null;
            currentChild = currentPushChild; 

        // Sinon, on explore un placement
        } else {
            currentChild = currentPlaceChild;
        }

        // On met à jour la profondeur du fils
        currentChild.setDepth(node.getDepth() + 1);

        // On récupère la grille du fils
        Grid childGrid = currentChild.getGrid();

        // On crée un nouvel itérateur de retrait pour le fils
        removGridIterator = new RemovGridIterator(childGrid, childGrid.getAlignments(node.getAgent().getColor(), 5));

        if (removGridIterator.hasNext())
            return createChild(childGrid)

        // On retourne le fils
        return child;
    }

}
