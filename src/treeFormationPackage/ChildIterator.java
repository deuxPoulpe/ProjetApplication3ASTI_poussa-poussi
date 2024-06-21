package treeFormationPackage;

import java.util.List;

import gamePackage.Coordinates;
import gamePackage.Grid;

public class ChildIterator {

    private GridTree node;
    private PlaceChildIterator placeChildIterator;
    private PushChildIterator pushChildIterator;
    private RemovGridIterator removGridIterator;
    private boolean isPushPhase = false;
    
    public ChildIterator(GridTree myNode) {
        this.node = myNode;
        this.placeChildIterator = new PlaceChildIterator(myNode);
    }

    public boolean hasNext() {
        // Si on a des jetons à retirer
        if (removGridIterator != null && removGridIterator.hasNext()) {
            return true;
        }
        // Si on est en phase de placement
        if (!isPushPhase) {
            return pushChildIterator.hasNext();
        }
        // Si on est en phase de poussée
        return placeChildIterator.hasNext();
    }

    public GridTree next() {

        // Si on est en phase de placement
        if (!isPushPhase) {

            // On met à jour l'itérateur de combinaisons de jetons à retirer
            Grid grid = node.getGrid();
            List<List<Coordinates>> alignments = grid.getAlignments(node.getAgent().getColor(), 5);
            removGridIterator = new RemovGridIterator(grid, alignments);
        }

         // Si aucun allignement de 5 n'a été formé par l'itération précédente
         if (removGridIterator == null || !removGridIterator.hasNext()) {

            // On crée un fils pour placer ou pousser un jeton
            GridTree child = createChild(node.getGrid());

            // On met à jour l'itérateur de combinaisons de jetons à retirer
            Grid childGrid = child.getGrid();
            List<List<Coordinates>> childAlignmentsOfFive = childGrid.getAlignments(node.getAgent().getColor(), 5);
            RemovGridIterator removGridIterator = new RemovGridIterator(childGrid, childAlignmentsOfFive);

            // Si on doit retirer des jetons
            if (removGridIterator.hasNext()) {

                // On retire la première combinaison de jetons
                CoordinateSetGridPair remCoordinateSetGridPair = removGridIterator.next();
                child.getRemovCoordinates().set(1, remCoordinateSetGridPair.getCoordinates());
                child.setGrid(remCoordinateSetGridPair.getGrid());
                return child;
            }
        }
        
        // Si un allignement de 5 a été formé par l'itération précédente, On retire la prochaine combinaison de jetons
        CoordinateSetGridPair remCoordinateSetGridPair = removGridIterator.next();
        
        // On met à jour la grille du noeud courant
        node.setGrid(remCoordinateSetGridPair.getGrid());
        
        // On crée un fils pour placer ou pousser un jeton
        GridTree child = createChild(remCoordinateSetGridPair.getGrid());

        // Si on est en début de tour, on met la combinaison de jetons à retirer dans la première case, sinon dans la deuxième
        int index = node.getDepth() == 0 && node.getPlaceCoordinates() == null ? 0 : 1;
        child.getRemovCoordinates().set(index, remCoordinateSetGridPair.getCoordinates());

        return child;
    }

    private GridTree createChild(Grid grid) {

        // Si on est en phase de placement
        if (!isPushPhase) {
            isPushPhase = true;
            GridTree child = placeChildIterator.next();

            // On met à jour l'itérateur de poussée sur le nouveau placement
            pushChildIterator = new PushChildIterator(child);
            return child;
        }
        // Sinon, on est en phase de poussée

        // On réinitialise les coordonnées à supprimer
        node.getRemovCoordinates().set(1, null);

        // Si on a exploré toutes les poussées pour le placement actuel, on passe au placement suivant
        if (!pushChildIterator.hasNext()) {
            isPushPhase = false;
            return next();
        }

        // Sinon, on retourne un fils pour pousser le jeton courant dans la direction courante
        return pushChildIterator.next();

    }
}
