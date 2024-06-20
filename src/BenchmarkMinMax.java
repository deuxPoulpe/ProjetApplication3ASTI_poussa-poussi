import myPackage.*;
import java.util.List;


public class BenchmarkMinMax {
    public static void main(String[] args) throws Exception {

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true);

        MinMaxAgent agent = new MinMaxAgent('Y', 1);

        Grid grid = new Grid();

        GridTree node = new GridTree(agent, grid);

        node.calculateHeuristicValue();

        System.out.println("Heuristic value: " + node.getHeuristicValue());

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