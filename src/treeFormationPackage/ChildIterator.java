package treeFormationPackage;

import java.util.List;

import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Settings;

public class ChildIterator {

    private ActionTree node;
    private PlaceChildIterator placeChildIterator;
    private PushChildIterator pushChildIterator;
    private RemovGridIterator removGridIterator;
    private boolean isPushPhase = false;
    
    public ChildIterator(ActionTree myNode) {
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
            return placeChildIterator.hasNext();
        }
        // Si on est en phase de poussée
        return pushChildIterator.hasNext();
    }

    public ActionTree next() {

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
            ActionTree child = createChild(node.getGrid());

            // On met à jour l'itérateur de combinaisons de jetons à retirer
            Grid childGrid = child.getGrid();
            List<List<Coordinates>> childAlignmentsOfFive = childGrid.getAlignments(node.getAgent().getColor(), 5);
            removGridIterator = new RemovGridIterator(childGrid, childAlignmentsOfFive);

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
        Grid remGrid = remCoordinateSetGridPair.getGrid();

        boolean isRoot = node.getDepth() == 0 && node.getPlaceCoordinates() == null;

        // On met à jour la grille du noeud courant
        if (isRoot) node.setGrid(remGrid);

        // On crée un fils pour placer ou pousser un jeton
        ActionTree child = createChild(remGrid);
        
        // Si on est en début de tour, on met la combinaison de jetons à retirer dans la première case, sinon dans la deuxième
        int index = isRoot ? 0 : 1;
        child.getRemovCoordinates().set(index, remCoordinateSetGridPair.getCoordinates());

        return child;
    }

    private ActionTree createChild(Grid grid) {
        // Vérifie si on doit passer à un nouveau placement
        if (!isPushPhase || !pushChildIterator.hasNext()) {
            if (placeChildIterator.hasNext()) {
                // Obtient le prochain placement
                ActionTree child = placeChildIterator.next();
                // Initialise l'itérateur de poussée pour ce nouveau placement
                pushChildIterator = new PushChildIterator(child);
                // Passe en phase de poussée
                isPushPhase = true;
                // Retourne le placement si les poussées ne sont pas obligatoires
                if (!Settings.getInstance().getMandatoryPush()) {
                    return child;
                }
            } else {
                // Si aucun autre placement n'est disponible, termine l'itération
                return null;
            }
        }

        // Si on est en phase de poussée et qu'il reste des poussées à traiter
        if (isPushPhase && pushChildIterator.hasNext()) {
            return pushChildIterator.next();
        } else {
            // Si toutes les poussées pour le placement actuel ont été traitées,
            // réinitialise pour traiter le prochain placement
            isPushPhase = false;
            // Appelle récursivement createChild pour passer au prochain placement
            return createChild(grid);
        }
    }
}
