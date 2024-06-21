import java.util.List;

import agentsPackage.MinMaxAgent;
import gamePackage.*;
import treeFormationPackage.ChildIterator;
import treeFormationPackage.PlaceChildIterator;
import treeFormationPackage.ActionTree;

public class BenchmarkMinMax {
    private static int depth = 1; // Ajout d'une valeur pour la profondeur de recherche
    private static int numberOfTests = 100; // Ajout d'une valeur pour le nombre de tests
    private static int nbTokenPerPlayer = 10; // Ajout d'une valeur pour le nombre de jetons par joueur
    public static long averageDurationBestMove = 0;

    public static void main(String[] args) throws Exception {

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true, false);
        
        // averageDurationBestMove = benchmarkBestMove(agent);
        
        // display();
        
        Grid grid = new Grid();
        
        // Grille avec un alignement de push optimal
        // grid.placeToken('Y', new Coordinates(0, 0));
        
        grid.display();
        
        MinMaxAgent agent = new MinMaxAgent('Y', depth);
        ActionTree root = new ActionTree(agent, grid);
        ActionTree bestMove = agent.evaluateBestMove(root, agent.getSmartness(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        System.out.println("Best move: \n" + bestMove + "\n");
        bestMove.getGrid().display();


    }

    public static void display() {
        String title = "\nBenchmark MinMax";
        String strDepth = "Depth: " + depth;
        String strNumberOfTests = "Number of tests: " + numberOfTests;
        String strNbTokenPerPlayer = "Number of tokens per player: " + nbTokenPerPlayer;
        String strDurationMinMax = "Average duration per test: " + averageDurationBestMove + " ms";
        String separator = "-------------------------";
        String result = title + "\n" + separator + "\n" + strDepth + "\n" + strNumberOfTests + "\n" + strNbTokenPerPlayer + "\n" + strDurationMinMax + "\n" + separator;
        System.out.println(result);
    }

    public static Grid generateRandomGrid(int nbTokenPerPlayer) {
        Grid grid = new Grid();
        List<Coordinates> emptyCellCoords = grid.getValidEmptyCoordinates();
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
        ActionTree root = new ActionTree(agent, grid);
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

            // Affiche une barre de progression
            double progress = (double) (i + 1) / numberOfTests;
            final int width = 50; // largeur de la barre de progression
            System.out.print("\r[");
            int j = 0;
            for (; j < (int) (progress * width); j++) {
                System.out.print("#");
            }
            for (; j < width; j++) {
                System.out.print(" ");
            }
            System.out.print("] " + Math.round(progress * 100) + "% ");
        }

        // Retourne la moyenne des durées d'exécution
        long sum = 0;
        for (long duration : durations) {
            sum += duration;
        }
        long average = sum / numberOfTests;
        return average;
    }
}