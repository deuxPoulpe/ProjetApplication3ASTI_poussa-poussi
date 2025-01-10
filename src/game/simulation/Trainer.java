package simulation;

import ai.NEAT.Population;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Trainer {

    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        // Make the user choose between creating a new population or loading an existing one
        System.out.println("1. Create a new population");
        System.out.println("2. Load an existing population");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        switch (choice) {
            case 1:
                // Make the user enter the population size
                System.out.print("Enter the population size: ");
                int populationSize = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                CreatePopulation(populationSize);
                break;
            case 2:
                // List available genome filenames
                java.io.File folder = new java.io.File("populations");
                java.io.File[] listOfFiles = folder.listFiles();
                if (listOfFiles != null) {
                    System.out.println("Available genomes:");
                    for (java.io.File file : listOfFiles) {
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            System.out.println(file.getName());
                        }
                    }
                } else {
                    System.out.println("No genomes available.");
                }

                // Make the user enter the filename
                System.out.print("Enter the filename: ");
                String filename = scanner.nextLine();
                Population population = loadPopulation(filename);
                if (population != null) {
                    savePopulation(population, "DEBUG");
                    System.out.println("Population loaded successfully.");

                    // Make the user enter the number of generations to train
                    System.out.print("Enter the number of generations to train: ");
                    int generations = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    // Inform the user that training is about to start
                    System.out.println("Training is about to start. Press Enter to continue.");
                    scanner.nextLine();

                    train(population, generations);
                } else {
                    System.out.println("Failed to load population.");
                }
                break;
            default:
                break;
        }
    }

    public static void train(Population population, int generations) {
        for (int i = 0; i < generations; i++) {
            System.out.println("Generation " + i);
            population.evolve();
        }
    }

    public static void CreatePopulation(int populationSize) {
        Population population = new Population(populationSize);
        System.out.print("Enter the name for the population: ");
        String populationName = scanner.nextLine();
        savePopulation(population, populationName);
    }

    public static void savePopulation(Population population, String populationName) {
        try {
            String json = population.toString();
            FileWriter writer = new FileWriter("populations/" + populationName + ".json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Population loadPopulation(String filename) {
        try {
            Scanner fileScanner = new Scanner(new java.io.File("populations/" + filename));
            StringBuilder json = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                json.append(fileScanner.nextLine());
            }
            fileScanner.close();
            // Assuming Population has a method to parse JSON string to Population object
            return Population.parse(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
