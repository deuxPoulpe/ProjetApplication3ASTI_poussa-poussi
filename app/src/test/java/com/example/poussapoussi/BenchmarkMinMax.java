package com.example.poussapoussi;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Random;

import agentsPackage.MinMaxAgent;
import gamePackage.*;

public class BenchmarkMinMax {
    private static int depth = 2; // Ajout d'une valeur pour la profondeur de recherche
    private static int numberOfTests = 1000; // Ajout d'une valeur pour le nombre de tests
    private static int nbTokenPerPlayer = 5; // Ajout d'une valeur pour le nombre de jetons par joueur
    public static long averageDurationBestMove = 0;
    private static final Random randomGenerator = new Random(); // Créez une instance de Random

    public static void main(String[] args) throws Exception {

        // Initialisation de l'agent MinMax
        MinMaxAgent agent = new MinMaxAgent('Y', depth, new int[]{0, 0});

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

        Coordinates randomCoordinates = new Coordinates(randomGenerator.nextInt(grid.getSize()), randomGenerator.nextInt(grid.getSize()));

        for (int i = 0; i < nbTokenPerPlayer; i++) {
            while (!grid.isFull() && !grid.isPlaceValid(randomCoordinates)) {
                randomCoordinates = new Coordinates(randomGenerator.nextInt(grid.getSize()), randomGenerator.nextInt(grid.getSize()));
                grid.placeToken('Y', randomCoordinates);
            }
        }
        for (int j=0; j< nbTokenPerPlayer; j++) {
            while (!grid.isFull() && !grid.isPlaceValid(randomCoordinates)) {
                randomCoordinates = new Coordinates(randomGenerator.nextInt(grid.getSize()), randomGenerator.nextInt(grid.getSize()));
                grid.placeToken('Y', randomCoordinates);
            }
        }

        return grid;
    }

    public static long measureBestMove(Grid grid, MinMaxAgent agent) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Long> task = () -> {
            long startTime = System.nanoTime();
            agent.evaluateAction(grid);
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
    
}