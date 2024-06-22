package treeFormationPackage;

import java.util.Set;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Settings;

public class ChildIterator implements Iterator<ActionTree>{

    private ActionTree node;
    private PlaceChildIterator placeChildIterator;
    private PushChildIterator pushChildIterator;
    private RemovGridIterator removGridIterator;
    private CoordinateSetGridPair currentRemCoordSetGridPair;
   
    public ChildIterator(ActionTree myNode) {
        this.node = myNode;
        Grid grid = node.getGrid();
        this.removGridIterator = new RemovGridIterator(grid, grid.getAlignments(node.getAgent().getColor(), 5));
    }

    @Override
    public boolean hasNext() {
        return removGridIterator.hasNext() || placeChildIterator.hasNext() || pushChildIterator.hasNext();
    }

    public ActionTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // On appelle la méthode pour obtenir le prochain fils
        return createChild(node.getGrid());
    }

    private ActionTree createChild(Grid grid) {

        // On ininitialise un fils
        ActionTree child = null;

        // Si on est en phase de poussée de jetons
        if (pushChildIterator != null && pushChildIterator.hasNext()) {

            // On récupère le prochain fils pour pousser dans une direction donnée
            child = pushChildIterator.next();

            // On met à jour l'itérateur de retrait de jetons
            removGridIterator = new RemovGridIterator(child.getGrid(), child.getGrid().getAlignments(node.getAgent().getColor(), 5));
        }
        else if (placeChildIterator != null && placeChildIterator.hasNext()) {

            // Sinon, si on a le droit de placer un jeton sans pousser, on itère sur le placement
            if (Settings.getInstance().getMandatoryPush()) {
                child = placeChildIterator.next();
                pushChildIterator = new PushChildIterator(child);
            }
            
            // Sinon, on explore le prochain placement
            else {
                
                // On appelle récursivement la méthode pour obtenir le prochain fils
                pushChildIterator = new PushChildIterator(child);
                child = createChild(grid);
            }
        }
        // Sinon, si on est en phase de retrait de jetons
        else if (removGridIterator != null && removGridIterator.hasNext()) {
                
                // On récupère la prochaine grille avec des jetons retirés, et les coordonnées des jetons retirés.
                currentRemCoordSetGridPair = removGridIterator.next();
                Grid remGrid = currentRemCoordSetGridPair.getGrid();
                Set<Coordinates> remCoords = currentRemCoordSetGridPair.getCoordinates();
    
                // On met à jour l'itérateur de placement de jetons
                ActionTree fakeChild = new ActionTree(node.getAgent(), remGrid);
                placeChildIterator = new PlaceChildIterator(fakeChild);

                // On appelle récursivement la méthode pour obtenir le prochain fils
                child = createChild(remGrid);

                // On met à jour les coordonnées de retrait de jetons
                boolean isRoot = node.getDepth() == 0;
                int index = isRoot ? 0 : 1;
                child.getRemovCoordinates().set(index, remCoords);
        }

        // On retourne le fils
        return child;
    }

}