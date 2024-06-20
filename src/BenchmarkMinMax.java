import java.util.List;
import myPackage.*;

public class BenchmarkMinMax {
    private static int depth = 2; // Ajout d'une valeur pour la profondeur de recherche
    private static int numberOfTests = 200; // Ajout d'une valeur pour le nombre de tests
    private static int nbTokenPerPlayer = 5; // Ajout d'une valeur pour le nombre de jetons par joueur
    public static long averageDurationBestMove = 0;

    public static void main(String[] args) throws Exception {

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true, false);
        
        MinMaxAgent agent = new MinMaxAgent('Y', depth);
        averageDurationBestMove = benchmarkBestMove(agent);
        
        display();
    }

    public static void display() {
        String title = "\nBenchmark MinMax";
        String strDepth = "Depth: " + depth;
        String strNumberOfTests = "Number of tests: " + numberOfTests;
        String strNbTokenPerPlayer = "Number of tokens per player: " + nbTokenPerPlayer;
        String strDurationMinMax = "Average duration per test: " + averageDurationBestMove + " ms";
        String separator = "-------------------------";
        String result = title + "\n" + strDepth + "\n" + strNumberOfTests + "\n" + strNbTokenPerPlayer + "\n" + strDurationMinMax + "\n" + separator;
        System.out.println(result);
    }

    public static Grid generateRandomGrid(int nbTokenPerPlayer) {
        Grid grid = new Grid();
        List<Coordinates> emptyCellCoords = grid.getValidEmptyCells();
        for (int i = 0; i < nbTokenPerPlayer; i++) {
            Coordinates randomCoords = emptyCellCoords.get((int) (Math.random() * emptyCellCoords.size()));
            grid.placeToken('Y', randomCoords);
            emptyCellCoords.remove(randomCoords);
        }
        for (int j=0; j< nbTokenPerPlayer; j++) {
            Coordinates randomCoords = emptyCellCoords.get((int) (Math.random() * emptyCellCoords.size())); 
            grid.placeToken('B', randomCoords);
            emptyCellCoords.remove(randomCoords);
        }

        return grid;
    }

    public static long measureBestMove(Grid grid, MinMaxAgent agent) {
        long startTime = System.nanoTime();
        GridTree root = new GridTree(agent, grid);
        agent.evaluateBestMove(root, agent.getSmartness(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        return duration;
    }

    public static long benchmarkBestMove(MinMaxAgent agent) {

        // Exécute le benchmark
        long[] durations = new long[numberOfTests];
        for (int i = 0; i < numberOfTests; i++) {
            Grid grid = generateRandomGrid(nbTokenPerPlayer);
            durations[i] = measureBestMove(grid, agent);
        }

        // Affiche la moyenne des durées d'exécution
        long sum = 0;
        for (long duration : durations) {
            sum += duration;
        }
        long average = sum / numberOfTests;
        return average;
    }
}