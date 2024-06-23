import agentsPackage.MinMaxAgent;
import gamePackage.Coordinates;
import gamePackage.Grid;
import gamePackage.PushAction;
import gamePackage.Settings;
import treeFormationPackage.ActionIterator;
import treeFormationPackage.ActionTree;
import treeFormationPackage.PushIterator;
import treeFormationPackage.RemovIterator;

public class GridTester {    
    public static void main(String[] args) throws Exception {

        Settings.getInstance(true, true, true);

        Grid grid = new Grid();
        grid.placeToken('Y', new Coordinates(0, 0));
        grid.placeToken('Y', new Coordinates(4, 0));
        grid.placeToken('Y', new Coordinates(7, 0));
        grid.placeToken('Y', new Coordinates(0, 4));
        grid.placeToken('Y', new Coordinates(7, 6));

        grid.placeToken('B', new Coordinates(3, 0));
        grid.placeToken('B', new Coordinates(0, 2));    
        grid.placeToken('B', new Coordinates(0, 3));
        grid.placeToken('B', new Coordinates(0, 6));
        grid.placeToken('B', new Coordinates(0, 7));

        grid.display();

        MinMaxAgent agent = new MinMaxAgent('Y', 2);
        ActionTree root = new ActionTree(agent, grid);

        // // AFFICHAGE DES ACTIONS
        // ActionIterator actionIterator = new ActionIterator(root);
        // int maxIter = 45;
        // int i = 0;
        // while (actionIterator.hasNext() && i < maxIter) {
        //     ActionTree action = actionIterator.next();
        //     System.out.println("Action: " + action);
        //     action.getGrid().display();
        //     i++;
        // }

        // AFFICHAGE DU MEILLEUR COUP
        ActionTree bestMove = agent.evaluateBestMove(root, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("Best move: " + bestMove);
        bestMove.getGrid().display();

        // // Affichage des grilles de retrait
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
    }

}
