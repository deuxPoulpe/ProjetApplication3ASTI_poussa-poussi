package treeFormationPackage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.HashSet;

import gamePackage.Grid;
import gamePackage.RemovAction;
import gamePackage.Settings;
import gamePackage.Action;

public class ActionIterator implements Iterator<ActionTree>{

    private ActionTree node;
    private ActionTree currentChild;
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

        boolean actionPending = true;

        while (actionPending) {

            // Si on a un prochain retrait en fin de tour
            if (endRemovIterator != null && endRemovIterator.hasNext()) {

                // On explore le prochain retrait en fin de tour
                RemovAction removAction = endRemovIterator.next();
                currentChild.getAction().setGrid(removAction.getGrid());
                currentChild.getAction().setEndRemove(removAction.getCoordinates());

                // On sort de la boucle
                actionPending = false;
            }

            // Si on a une prochaine poussée
            else if (pushIterator != null && pushIterator.hasNext()) {

                // On explore la prochaine poussée
                currentChild = pushIterator.next();

                // On réinitialise l'itérateur de retrait en fin de tour sur la prochaine grille de poussée
                endRemovIterator = new RemovIterator(currentChild.getAction().getGrid(), currentChild.getAgent().getColor());

                // Si on n'a pas de retrait en fin de tour, on sort de la boucle
                if (!endRemovIterator.hasNext()) {
                    actionPending = false;
                }
            }

            // Si on a un prochain placement
            else if (placeIterator != null && placeIterator.hasNext()) {

                // On explore le prochain placement
                currentChild = placeIterator.next();
                
                // On réinitialise l'itérateur de poussée sur le prochain placement
                pushIterator = new PushIterator(currentChild);
                
                // Si on a le droit de ne pas pousser
                if (!Settings.getInstance().getMandatoryPush()) {
                    // On réinitialise l'itérateur de retrait en fin de tour sur la prochaine grille de placement
                    endRemovIterator = new RemovIterator(currentChild.getAction().getGrid(), currentChild.getAgent().getColor());
                    if (!endRemovIterator.hasNext() && !Settings.getInstance().getMandatoryPush()) {
                        actionPending = false;
                    }
                }
            }

            // Si on a un prochain retrait en début de tour
            else if (startRemovIterator != null && startRemovIterator.hasNext()) {

                // On explore le prochain retrait en début de tour
                RemovAction removAction = startRemovIterator.next();
 

                Action childAction = new Action(removAction.getCoordinates(), null, null, new HashSet<>(), removAction.getGrid());
                currentChild = new ActionTree(node, childAction);

                // On met à jour la grille et les coordonnées de retrait de jetons du prochain enfant
                currentChild.getAction().setGrid(removAction.getGrid());
                currentChild.getAction().setStartRemove(removAction.getCoordinates());

                // On réinitialise l'itérateur de placement sur le prochain retrait en début de tour
                placeIterator = new PlaceIterator(currentChild);

                // Si on n'a pas de placement, on sort de la boucle
                if (!placeIterator.hasNext()) {
                    actionPending = false;
                }
            }
        }

        return currentChild;
    }
}