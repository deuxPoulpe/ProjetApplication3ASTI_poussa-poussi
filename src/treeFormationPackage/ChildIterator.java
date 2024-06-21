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
    private ActionTree currentChild;
   
    public ChildIterator(ActionTree myNode) {
        this.node = myNode;
        this.placeChildIterator = new PlaceChildIterator(node);;
        Grid grid = node.getGrid();
        this.removGridIterator = new RemovGridIterator(grid, grid.getAlignments(node.getAgent().getColor(), 5));
    }

    @Override
    public boolean hasNext() {
        return placeChildIterator.hasNext() || removGridIterator.hasNext();
    }

    public ActionTree next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        ActionTree child = null;

        // Si on est en phase de retrait de jetons
        if (removGridIterator.hasNext()) {

            // On récupère la prochaine grille avec des jetons retirés, et les coordonnées des jetons retirés.
            currentRemCoordSetGridPair = removGridIterator.next();
            Grid remGrid = currentRemCoordSetGridPair.getGrid();
            Set<Coordinates> remCoords = currentRemCoordSetGridPair.getCoordinates();

            boolean isRoot = node.getDepth() == 0;
            if (isRoot) node.setGrid(remGrid);

            // On crée un fils avec la grille obtenue après avoir retiré les jetons.
            child = createChild(remGrid);

            // On met à jour les coordonnées des jetons retirés.
            // Le premier index est l'ensemble des jetons retirés en début de tour, le deuxième est l'ensemble des jetons retirés en fin de tour.
            // Si on est à la racine, on met à jour le premier index, sinon on met à jour le deuxième index.
            int index = isRoot ? 0 : 1;
            child.getRemovCoordinates().set(index, remCoords);
        }
        else {
            // Si on est en phase de placement de jetons

            // On crée un fils avec la grille actuelle.
            child = createChild(node.getGrid());

            // On met à jour l'itérateur de retrait de jetons.
            Grid childGrid = child.getGrid();
            removGridIterator = new RemovGridIterator(childGrid, childGrid.getAlignments(node.getAgent().getColor(), 5));

            // Si un alignement de 5 jetons a été formé, on passe à la phase de retrait de jetons.
            if (removGridIterator.hasNext()) {

                // On récupère la prochaine grille avec des jetons retirés, et les coordonnées des jetons retirés.
                currentRemCoordSetGridPair = removGridIterator.next();
                Grid remGrid = currentRemCoordSetGridPair.getGrid();
                Set<Coordinates> remCoords = currentRemCoordSetGridPair.getCoordinates();

                // On met à jour la grille et les coordonnées des jetons retirés du fils.
                child.setGrid(remGrid);
                child.getRemovCoordinates().set(1, remCoords);
            }

        }

        return child;
    }

    private ActionTree createChild(Grid grid) {

        // On ininitialise un fils
        ActionTree child = currentChild;

        // Si on est en phase de poussée de jetons
        if (pushChildIterator != null && pushChildIterator.hasNext()) {

            // On récupère le prochain fils pour pousser dans une direction donnée
            child = pushChildIterator.next();
        }

        // Sinon, si on a le droit de placer un jeton sans pousser, on itère sur le placement sans le retourner
        else if (Settings.getInstance().getMandatoryPush()) {
            pushChildIterator = new PushChildIterator(placeChildIterator.next());

            // On appelle récursivement la méthode pour obtenir le prochain fils
            child = createChild(grid);
        }

        // Sinon, on explore le prochain placement
        else {
            child = placeChildIterator.next();
            pushChildIterator = new PushChildIterator(child);
        }
        // On retourne le fils
        return child;
    }
}
