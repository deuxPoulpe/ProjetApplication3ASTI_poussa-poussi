import agentsPackage.MinMaxAgent;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.Settings;
import gamePackage.Action;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import treeFormationPackage.ActionTree;
import treeFormationPackage.PlaceIterator;
import treeFormationPackage.ActionIterator;
import treeFormationPackage.PushIterator;
import treeFormationPackage.RemovIterator;

public class GridTester {    
    public static void main(String[] args) throws Exception {

        Settings.getInstance(true, true, true);

        Grid grid = new Grid();
        grid.placeToken('Y', new Coordinates(7, 0));
        grid.placeToken('Y', new Coordinates(6, 0));
        grid.placeToken('Y', new Coordinates(5, 0));
        grid.placeToken('Y', new Coordinates(4, 0));

        // grid.placeToken('B', new Coordinates(3, 0));
        // grid.placeToken('B', new Coordinates(0, 2));    
        // grid.placeToken('B', new Coordinates(0, 3));
        // grid.placeToken('B', new Coordinates(0, 6));
        // grid.placeToken('B', new Coordinates(0, 7));

        grid.display();

        MinMaxAgent agent = new MinMaxAgent('Y', 1);
        ActionTree root = new ActionTree(agent, grid);

        // // AFFICHAGE DES ACTIONS
        // ActionIterator actionIterator = new ActionIterator(root);
        // int maxIter = 10000;
        // int i = 0;
        // while (actionIterator.hasNext() && i < maxIter) {
        //     ActionTree action = actionIterator.next();
        //     System.out.println("Action: " + action);
        //     action.getAction().getGrid().display();
        //     i++;
        //     if (action.getAction().getEndRemove().size() > 0) {
        //         break;
        //     }
        // }

        // AFFICHAGE DU MEILLEUR COUP
        Action bestMove = agent.evaluateAction(grid);
        System.out.println("Best move: " + bestMove);
        
        ActionTree bestAction = new ActionTree(agent, grid);
        bestAction.setAction(bestMove);
        bestAction.calculateHeuristicValue();
        System.out.println("Heuristic value: " + bestAction.getHeuristicValue());

        // // // Affichage des grilles de retrait
        // RemovIterator removIterator = new RemovIterator(grid, 'Y');
        // int maxIter = 45;
        // int i = 0;
        // while (removIterator.hasNext() && i < maxIter) {
        //     Grid gridRemov = removIterator.next().getGrid();
        //     gridRemov.display();
        //     i++;
        // }
        // System.out.println("Nombre de grilles de retrait: " + i);

        // // Affichage des grilles de poussée
        // PushIterator pushIterator = new PushIterator(root);
        // int maxIter = 45;
        // int i = 0;
        // while (pushIterator.hasNext() && i < maxIter) {
        //     ActionTree push = pushIterator.next();
        //     System.out.println("Push: " + push);
        //     push.getGrid().display();
        //     i++;
        // }
        // System.out.println("Nombre de grilles de poussée: " + i);

        // // TEST DE LA POUSSÉE
        // PushAction pushAction = new PushAction(new Coordinates(7, 0), new int[]{0, -1});
        // boolean isPushValid = grid.isPushValid(pushAction, 'Y');
        // System.out.println("Is push valid: " + isPushValid);
        // grid.pushToken(pushAction, 'Y');
        // grid.display();

        // // AFFICHAGE DES GRILLES DE PLACEMENT
        // PlaceIterator placeIterator = new PlaceIterator(root);
        // int maxIter = 100;
        // int i = 0;
        // while (placeIterator.hasNext() && i < maxIter) {
        //     ActionTree action = placeIterator.next();
        //     System.out.println("Action: " + action);
        //     action.getAction().getGrid().display();
        //     i++;
        // }

        // // CALCUL DE LA VALEUR HEURISTIQUE
        // root.calculateHeuristicValue();
        // System.out.println("Heuristic value: " + root.getHeuristicValue());
        // Set<Coordinates> endRemove = new HashSet<>();
        // endRemove.add(new Coordinates(0, 0));
        // endRemove.add(new Coordinates(0, 1));
        // ActionTree actionTree = new ActionTree(agent, grid);
        // actionTree.getAction().setEndRemove(endRemove);
        // actionTree.getAction().setPlacement(new Coordinates(0, 4));
        // actionTree.calculateHeuristicValue();
        // System.out.println("Heuristic value: " + actionTree.getHeuristicValue());
    }

}
