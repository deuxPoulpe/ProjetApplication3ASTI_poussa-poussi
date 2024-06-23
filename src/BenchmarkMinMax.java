import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import agentsPackage.MinMaxAgent;
import gamePackage.*;
import treeFormationPackage.ActionIterator;
import treeFormationPackage.ActionTree;
import treeFormationPackage.PushIterator;

public class BenchmarkMinMax {
    private static int depth = 2; // Ajout d'une valeur pour la profondeur de recherche
    private static int numberOfTests = 1000; // Ajout d'une valeur pour le nombre de tests
    private static int nbTokenPerPlayer = 5; // Ajout d'une valeur pour le nombre de jetons par joueur
    public static long averageDurationBestMove = 0;

    public static void main(String[] args) throws Exception {

        // Initialisation de l'agent MinMax
        MinMaxAgent agent = new MinMaxAgent('Y', depth);

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true, false);
        
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Long> task = () -> {
            long startTime = System.nanoTime();
            ActionTree root = new ActionTree(agent, grid);
            agent.evaluateBestMove(root, agent.getSmartness(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            long endTime = System.nanoTime();
            return (endTime - startTime) / 1000000;
        };

        Future<Long> future = executor.submit(task);
        long duration;
        try {
            // Attendre la fin de l'exécution ou timeout après 3 secondes
            duration = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Gérer le cas où l'exécution dépasse 5 secondes
            System.out.println("\n Le temps est dépassé pour l'exécution de measureBestMove. Affichage de la grille actuelle :");
            grid.display(); // Affiche la grille actuelle
            duration = -1; // Retourner une valeur spécifique ou gérer autrement
        } catch (Exception e) {
            // Gérer les autres exceptions
            duration = -1; // Retourner une valeur spécifique ou gérer autrement
        } finally {
            executor.shutdownNow(); // Arrêter l'executor immédiatement
        }

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
    
    // Méthode pour effacer le terminal
    private static void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
}