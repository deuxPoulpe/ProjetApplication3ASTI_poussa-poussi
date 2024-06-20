import myPackage.*;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;


public class BenchmarkMinMax {
    public static void main(String[] args) throws Exception {

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true);

        MinMaxAgent agent = new MinMaxAgent('Y', 1);

        Grid grid = new Grid();
        // Crée un alignement de 5 jetons pour le joueur
        grid.placeToken('Y', new Coordinates(0, 0));
        grid.placeToken('Y', new Coordinates(0, 1));
        grid.placeToken('Y', new Coordinates(0, 2));
        grid.placeToken('Y', new Coordinates(0, 3));
        grid.placeToken('Y', new Coordinates(0, 4));

        GridTree node = new GridTree(agent, grid);
        HashMap<Set<Coordinates>, Grid> removGrids = node.getRemovMap(grid);
        List<GridTree> children = new ArrayList<>();
        for (Set<Coordinates> coordsToRemoveSet : removGrids.keySet()) {
            GridTree child = new GridTree(agent, removGrids.get(coordsToRemoveSet));
            child.getRemovCoordinates().get(1).addAll(coordsToRemoveSet);
            children.add(child);
        }
        System.out.println("Children: " + children);


    }

    public static Grid generateRandomGrid(int nbTokenPerPlayer) {
        Grid grid = new Grid();
        List<Coordinates> emptyCellCoords = grid.getValidEmptyCells();
        for (int i = 0; i < nbTokenPerPlayer; i++) {
            Coordinates randomCoords = emptyCellCoords.get((int) (Math.random() * emptyCellCoords.size()));
            grid.placeToken('Y', randomCoords);
            emptyCellCoords.remove(randomCoords);
        }
        return grid;
    }

    public static long measureBestMove(Grid grid, MinMaxAgent agent, int depth) {
        long startTime = System.nanoTime();
        // Ajoutez le deuxième argument nécessaire pour la méthode evaluateBestMove
        GridTree root = new GridTree(agent, grid);
        GridTree bestMove = agent.evaluateBestMove(root, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("Best move : " + bestMove.toString());
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        return duration;
    }

    public static long benchmarkMinMax(Grid grid, MinMaxAgent agent, int depth, int numberOfTests) {

        // Exécute le benchmark
        long[] durations = new long[numberOfTests];
        for (int i = 0; i < numberOfTests; i++)
            measureBestMove(grid, agent, depth);

        // Affiche la moyenne des durées d'exécution
        long sum = 0;
        for (long duration : durations) {
            sum += duration;
        }
        long average = sum / numberOfTests;
        return average;
    }
    
}