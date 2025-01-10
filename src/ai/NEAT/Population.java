package ai.NEAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import core.Board;
import core.GameLoop;
import agents.GenomeAgent;

import java.util.Map;

public class Population {
    private int populationSize;
    private List<Genome> genomes;
    private List<Species> species = new ArrayList<>();
    private Map<Genome, Species> speciesMap = new HashMap<>();
    private List<Genome> nextGeneration;
    private Genome fittestGenome;
    private double highestFitness;

    private final Random random = new Random();
    private final Counter nodeCounter = new Counter();
    private final Counter connectionCounter = new Counter();

    // Hyperparameters
    private static final int BOARD_SIZE = 8; // Size of the game board
    private static final double DELTA_THRESHOLD = 3.0;
    private static final double MUTATION_RATE = 0.1;
    private static final double ADD_CONNECTION_RATE = 0.05;
    private static final double ADD_NODE_RATE = 0.03;

    // Environment of the simulation
    private final GameLoop game = new GameLoop();
    {
        game.setBoard(new Board(BOARD_SIZE));
        game.setPlayer1(new GenomeAgent(1, null));
        game.setPlayer2(new GenomeAgent(2, null));
    }

    public Population() {
        this.genomes = new ArrayList<>();
    }

    public Population(int populationSize) {
        this.populationSize = populationSize;
        this.genomes = new ArrayList<>();
        initialize();
    }

    public List<Genome> getNextGeneration() {
        return nextGeneration;
    }

    public Genome getFittestGenome() {
        return fittestGenome;
    }

    /**
     * Initialize the population with random mutations
     */
    private void initialize() {
        for (int i = 0; i < populationSize; i++) {
            Genome genome = createInitialGenome();
            genome.mutate(random);
            genomes.add(genome);
        }
    }

    /**
     * Create an initial genome with input and output nodes for each cell on the board
     * 
     * The first 3 * BOARD_SIZE^2 nodes are input nodes for each cell on the board.
     * A period of 3 nodes is used to represent each cell, with the first node representing an empty cell,
     * the second node representing a cell owned by the player, and the third node representing a cell owned by the opponent.
     * 
     * The next BOARD_SIZE^2 nodes are output nodes for each possible removal on the board.
     * 
     * The next BOARD_SIZE^2 nodes are output nodes for each possible placement on the board.
     * 
     * The final 4 * BOARD_SIZE^2 nodes are output nodes for each possible push on the board.
     * A Period of 4 nodes is used to represent each push, with the first node representing a push up,
     * the second node representing a push down, the third node representing a push left, and the fourth node representing a push right.
     * @return The initial genome
     */
    private Genome createInitialGenome() {
        Genome genome = new Genome();
    
        int playerCount = 2; // Number of players in the game
        int inputSize = BOARD_SIZE * BOARD_SIZE * (playerCount + 1);

        int pushDirectionNumber = 4; // Number of directions to push a piece (up, down, left, right)
        int outputSize = BOARD_SIZE * BOARD_SIZE * (1 + 1 + pushDirectionNumber); // 1 for removal, 1 for placement, pushDirectionNumber for push
    
        // Create input nodes
        for (int i = 0; i < inputSize; i++) {
            genome.addNode(new Node(Node.Type.INPUT, nodeCounter.nextNumber()));
        }

        // Create output nodes
        for (int i = 0; i < outputSize; i++) {
            Node outputNode = new Node(Node.Type.OUTPUT, nodeCounter.nextNumber());
            genome.addNode(outputNode);

            // Connect each input node to the output node
            for (Node inputNode : genome.getNodes().values()) {
                if (inputNode.getType() == Node.Type.INPUT) {
                    genome.addConnection(new Connection(inputNode.getId(), outputNode.getId(), random.nextDouble() * 2 - 1, true, connectionCounter.nextNumber()));
                }
            }
        }
        return genome;
    }    

    /**
     * Assign each genome to a species based on compatibility distance
     */
    private void mapGenomesToSpecies() {
        // Add the genome to an existing species if it's compatibility distance is below a threshold
        for (Genome genome : genomes) {            
            boolean speciesFound = false;
            for (Species species : species) {
                if (Genome.computeCompatibilityDistance(genome, species.getRepresentative()) < DELTA_THRESHOLD) {
                    speciesMap.put(genome, species);
                    speciesFound = true;
                    break;
                }
            }
            // Create a new species with the genome as the representative if no species is found
            if (!speciesFound) {
                Species newSpecies = new Species(genome);
                newSpecies.add(genome);
                speciesMap.put(genome, newSpecies);
                species.add(newSpecies);
            }
        }
    }

    /**
     * Evaluate the fitness of each genome in the population and update the species average fitness
     * @param boardSize The size of the game board
     * @param simulationNumber The number of simulations to run for each genome
     */
    private void evaluate() {

        ((GenomeAgent) game.getPlayer1()).setGenome(genomes.get(0));
        // Evaluate the fitness of each genome
        int count = 0;
        for (Genome genome : genomes) {
            count++;

            System.out.println("Evaluating genome: " + count);

            // Get the species of the genome
            Species species = speciesMap.get(genome);
            
            // Evaluate the genome
            double fitness = genome.evaluate(game);
            genome.setFitness(fitness);

            // Update the fittest genome of the population
            if (fitness > highestFitness) {
                highestFitness = genome.getFitness();
                fittestGenome = genome;
            }
            
            // Update the species average fitness
            double adjustedFitness = fitness / speciesMap.get(genome).genomes.size();
            species.addAdjustedFitness(adjustedFitness);
        }
    }

    /**
     * Populate the next generation of genomes
     * 
     * The next generation is created by selecting the fittest genomes from each species,
     * then breeding them to create new genomes. 
     */
    private void generateNextGeneration() {

        List<Genome> nextGeneration = new ArrayList<>();
        for (Species species : speciesMap.values()) {
            nextGeneration.add(species.getFittestGenome());
        }

        // Breed the next generation
        while (nextGeneration.size() < populationSize) {
            // Select a random species biased towards the fittest species
            Species species = selectSpeciesByFitnessProportion();

            // Select two random parents from the species
            Genome parent1 = species.genomes.get(random.nextInt(species.genomes.size()));
            Genome parent2 = species.genomes.get(random.nextInt(species.genomes.size()));

            // Ensure parent1 is the fittest parent
            if (parent1.getFitness() < parent2.getFitness()) {
                Genome temp = parent1;
                parent1 = parent2;
                parent2 = temp;
            }

            // Crossover the parents
            Genome child = Genome.crossover(parent1, parent2, random);

            // Mutate the child
            if (random.nextDouble() < MUTATION_RATE) {
                child.mutate(random);
            }
            if (random.nextDouble() < ADD_CONNECTION_RATE) {
                child.addConnectionMutation(random, connectionCounter);
            }

            if (random.nextDouble() < ADD_NODE_RATE) {
                child.addNodeMutation(random, nodeCounter, connectionCounter);
            }

            // Add the child to the next generation
            nextGeneration.add(child);
        }
    }

    public void evolve() {

        // Map each genome to its species and store the fittest genome of the population
        mapGenomesToSpecies();

        // Evaluate the fitness of each genome and species
        evaluate();

        // Populate the next generation
        generateNextGeneration();

        // Replace the current generation with the next generation
        genomes = nextGeneration;

        // Clear the next generation, reset the species and fittest genome
        nextGeneration.clear();
         speciesMap.clear();
         species.clear();
         fittestGenome = null;
    }

    /**
     * Select a species based on fitness proportionate selection
     * 
     * Uses the roulette wheel selection method to select a random species.
     * The probability of selecting a species is proportional to the average fitness of the species.
     * 
     * @return The selected species
     */
    private Species selectSpeciesByFitnessProportion() {
        // Sum the average fitness of all species
        double totalAverageFitness = 0;
        for (Species species : speciesMap.values()) {
            totalAverageFitness += species.averageFitness;
        }

        // Generate a random number between 0 and the total average fitness
        double randomFitness = random.nextDouble() * totalAverageFitness;

        // Select the species that corresponds to the random fitness
        double currentFitness = 0;
        for (Species species : speciesMap.values()) {
            currentFitness += species.averageFitness;
            if (currentFitness >= randomFitness) {
                return species;
            }
        }

        return null;
    }

    private class Species {
        private List<Genome> genomes;
        private Genome representative;
        private double highestFitness;
        private double averageFitness;
        private Genome fittestGenome;

        public Species(Genome representative) {
            this.genomes = new ArrayList<>();
            this.representative = representative;
        }

        // Add a genome to the species, keep the list sorted by fitness
        public void add(Genome genome) {
            if (genome.getFitness() > highestFitness) {
                highestFitness = genome.getFitness();
                fittestGenome = genome;
            }
            genomes.add(genome);
        }

        public Genome getFittestGenome() {
            return fittestGenome;
        }

        public Genome getRepresentative() {
            return representative;
        }

        public void addAdjustedFitness(double adjustedFitness) {
            averageFitness += adjustedFitness;
        }
        }

    @Override
    public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(populationSize + "$");
    for (int i = 0; i < genomes.size(); i++) {
        sb.append(genomes.get(i).toString());
        if (i < genomes.size() - 1) {
        sb.append("$");
        }
    }
    return sb.toString();
    }

    public static Population parse(String json) {
        Population population = new Population();
        String[] parts = json.split("\\$");
        population.populationSize = Integer.parseInt(parts[0]);
        System.out.println("Population size: " + population.populationSize);
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].length() > 0) {
            Genome genome = Genome.parse(parts[i]);
            population.genomes.add(genome);
            }
        }
        return population;
        }
    }
