package treeFormationPackage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamePackage.Grid;
import gamePackage.RemovAction;
import gamePackage.Settings;

public class ActionIterator implements Iterator<ActionTree>{

    private ActionTree node;
    private PlaceIterator placeIterator;
    private PushIterator pushIterator;
    private RemovIterator startRemovIterator;
    private RemovIterator endRemovIterator;
    
    public ActionIterator(ActionTree myNode) {
        this.node = myNode;
        Grid grid = node.getAction().getGrid();

        // On initialise l'itérateur de retrait en début de tour
        this.startRemovIterator = new RemovIterator(grid, node.getAgent().getColor());

        // Si on n'a pas de retrait en début de tour, on initialise l'itérateur de placement
        if (!startRemovIterator.hasNext()) {
            this.placeIterator = new PlaceIterator(node);
        }
    }

    @Override
    public boolean hasNext() {
        return startRemovIterator.hasNext() || placeIterator.hasNext() || pushIterator.hasNext() || endRemovIterator.hasNext();
    }

    @Override
    public ActionTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
     
        ActionTree child = new ActionTree(node.getAgent(), node.getAction().getGrid());
        boolean actionPending = true;
        
        while (actionPending) {

            // Si on a un prochain retrait en fin de tour
            if (endRemovIterator != null && endRemovIterator.hasNext()) {

                // On explore le prochain retrait en fin de tour
                RemovAction removAction = endRemovIterator.next();
                child.getAction().setGrid(removAction.getGrid());

                if (node.getDepth() == 0) {
                    child.getAction().setStartRemove(removAction.getCoordinates());
                }
                else {
                    child.getAction().setEndRemove(removAction.getCoordinates());
                }

                // On sort de la boucle
                actionPending = false;
            }

            // Si on a une prochaine poussée
            else if (pushIterator != null && pushIterator.hasNext()) {

                // On explore la prochaine poussée
                child = pushIterator.next();

                // On réinitialise l'itérateur de retrait en fin de tour sur la prochaine grille de poussée
                endRemovIterator = new RemovIterator(child.getAction().getGrid(), child.getAgent().getColor());

                // Si on n'a pas de retrait en fin de tour, on sort de la boucle
                actionPending = false;
                if (!endRemovIterator.hasNext()) {
                    actionPending = false;
                }
            }

            // Si on a un prochain placement
            else if (placeIterator != null && placeIterator.hasNext()) {

                // On explore le prochain placement
                child = placeIterator.next();

                // On réinitialise l'itérateur de poussée sur le prochain placement
                pushIterator = new PushIterator(child);

                // Si on n'a pas de poussée obligatoire
                if (!Settings.getInstance().getMandatoryPush()) {
                    
                    // On réinitialise l'itérateur de retrait en fin de tour sur la prochaine grille de placement
                    endRemovIterator = new RemovIterator(child.getAction().getGrid(), child.getAgent().getColor());
                    if (!endRemovIterator.hasNext()) {
                        actionPending = false;
                    }
                }
                // Si on n'a pas de retrait en fin de tour, on sort de la boucle
                if (endRemovIterator == null ? !pushIterator.hasNext() : !endRemovIterator.hasNext() && !pushIterator.hasNext()) {
                    actionPending = false;
                }
            }

            // Si on a un prochain retrait en début de tour
            else if (startRemovIterator.hasNext()) {

                // On explore le prochain retrait en début de tour
                RemovAction removAction = startRemovIterator.next();

                // On met à jour la grille et les coordonnées de retrait de jetons du prochain enfant
                child.getAction().setGrid(removAction.getGrid());
                child.getAction().setStartRemove(removAction.getCoordinates());

                // On réinitialise l'itérateur de placement sur le prochain retrait en début de tour
                placeIterator = new PlaceIterator(child);

                // Si on n'a pas de placement, on sort de la boucle
                if (!placeIterator.hasNext()) {
                    actionPending = false;
                }
            }
        }
        
        return child;
    }
}