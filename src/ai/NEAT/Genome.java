package ai.NEAT;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import core.GameLoop;

public class Genome {
    private Map<Integer, Connection> connections; // Innovation number, Connection
    private Map<Integer, Node> nodes; // Id, Node
    private double fitness;

    // Compatibility distance coefficients
    private static final double C1 = 1.0;
    private static final double C2 = 1.0;
    private static final double C3 = 0.4;

    private static final double PROBABILITY_PERTURBING = 0.9; // Probability of perturbing the weight of a connection during mutation
    private final int SIMULATION_NUMBER = 10; // Number of simulations to run for each genome
    
    public Genome() {
        this.connections = new HashMap<>();
        this.nodes = new HashMap<>();
    }

    public Genome(Map<Integer, Connection> connections, Map<Integer, Node> nodes) {
        this.connections = connections;
        this.nodes = nodes;
    }

    public Map<Integer, Connection> getConnections() {
        return connections;
    }

    public Map<Integer, Node> getNodes() {
        return nodes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setConnections(Map<Integer, Connection> connections) {
        this.connections = connections;
    }

    public void setNodes(Map<Integer, Node> nodes) {
        this.nodes = nodes;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public void addConnection(Connection connection) {
        connections.put(connection.getId(), connection);
    }

    /**
     * Add a connection to the genome
     * @param random
     * @param counter
     */
    public void addConnectionMutation(Random random, Counter counter) {
        // Select two random nodes
        Integer[] keys = nodes.keySet().toArray(new Integer[0]);
        Node node1 = nodes.get(keys[random.nextInt(keys.length)]);
        Node node2 = nodes.get(keys[random.nextInt(keys.length)]);

        // Generate a random weight
        double weight = random.nextDouble() * 4d - 2d;

        // Check in which direction the connection should be created
        boolean reversed = false;
        if (node1.getType() == Node.Type.HIDDEN && node2.getType() == Node.Type.INPUT) {
            reversed = true;
        } else if (node1.getType() == Node.Type.OUTPUT && node2.getType() == Node.Type.HIDDEN) {
            reversed = true;
        } else if (node1.getType() == Node.Type.OUTPUT && node2.getType() == Node.Type.INPUT) {
            reversed = true;
        }
        
        // Check if the connection already exists
        boolean connectionExists = false;
        for (Connection connection : connections.values()) {
            // Check if the connection already exists in both directions
            if (connection.getInNode() == node1.getId() && connection.getOutNode() == node2.getId()) {
                connectionExists = true;
                break;
            }
            if (connection.getInNode() == node2.getId() && connection.getOutNode() == node1.getId()) {
                connectionExists = true;
                break;
            }
        }

        // If the connection already exists, return
        if (connectionExists) return;
        
        // If the connection does not exist, add it to the genome
        if (reversed) { 
            Connection newConnection = new Connection(node2.getId(), node1.getId(), weight, true, counter.nextNumber());
            connections.put(newConnection.getId(), newConnection);
        }
        else {
            Connection newConnection = new Connection(node1.getId(), node2.getId(), weight, true, counter.nextNumber());
            connections.put(newConnection.getId(), newConnection);
        }
    }

    /**
     * Add an intermediate node between two random connected nodes
     * @param random
     * @param nodeCounter
     * @param connectionCounter
     */
    public void addNodeMutation(Random random, Counter nodeCounter, Counter connectionCounter) {
        // Select a random connection
        Connection connection = connections.get(random.nextInt(connections.size()));

        // Get the in and out nodes of the connection
        Node inNode = nodes.get(connection.getInNode());    
        Node outNode = nodes.get(connection.getOutNode());

        // Disable the connection
        connection.setEnabled(false);

        // Create a new hidden node
        Node hiddenNode = new Node(Node.Type.HIDDEN, nodeCounter.nextNumber());

        // Add an intermediate node between the in and out nodes
        Connection inToHidden = new Connection(inNode.getId(), hiddenNode.getId(), 1, true, connectionCounter.nextNumber());
        Connection hiddenToOut = new Connection(hiddenNode.getId(), outNode.getId(), connection.getWeight(), true, connectionCounter.nextNumber());

        // Add the new nodes and connections to the genome
        nodes.put(hiddenNode.getId(), hiddenNode);
        connections.put(inToHidden.getId(), inToHidden);
        connections.put(hiddenToOut.getId(), hiddenToOut);
    }

    /**
     * Crossover two genomes
     * @param parent1 More fit parent
     * @param parent2 Less fit parent
     * @return Child genome with a mix of the two parents' genes
     */
    public static Genome crossover(Genome parent1, Genome parent2, Random random) {

        Genome child = new Genome();

        // Add all nodes from the more fit parent
        for (Node node : parent1.getNodes().values()) {
            child.addNode(node.clone());
        }

        for (Connection connection : parent1.getConnections().values()) {
            // If we have a matching node, randomly select one of the two connections
            if (parent2.getConnections().containsKey(connection.getId())) {
                Connection childConnection = random.nextBoolean() ? connection.clone() : parent2.getConnections().get(connection.getId()).clone();
                child.addConnection(childConnection);
                
            }
            // Else if we have a disjoint/excess node, add it to the child
            else {
                Connection childConnection = connection.clone();
                child.addConnection(childConnection);
            }
        }
        return child;

        }

    /**
     * Mutate the genome
     * @param random
     */
    public void mutate(Random random) {
        for (Connection connection : connections.values()) {
            if (random.nextDouble() < PROBABILITY_PERTURBING) {
                connection.setWeight(connection.getWeight() + (random.nextDouble() * 4d - 2d));
            }
            else {
                connection.setWeight(random.nextDouble() * 4d - 2d);
            }
        }
    }

    /**
     * Compute the compatibility distance between two genomes
     * 
     * Uses the formula: excessGenes * C1 / N + disjointGenes * C2 / N + averageWeightDifference * C3
     * The constants C1, C2, and C3 are used to scale the contribution of each term
     * 
     * N can be neglected to 1 for small genomes
     * 
     * @param genome1 
     * @param genome2
     * @return 
     */
    public static double computeCompatibilityDistance(Genome genome1, Genome genome2) {
        int matchingGenes = 0;
        int disjointGenes = 0;
        int excessGenes = 0;
        double weightDifference = 0.0;

        int N = 1; // Number of genes in the larger genome

        List<Integer> tmpList1 = genome1.getNodes().keySet().stream().sorted().toList();
        List<Integer> tmpList2 = genome2.getNodes().keySet().stream().sorted().toList();

        int highestInnovation1 = tmpList1.isEmpty() ? 0 : tmpList1.get(tmpList1.size() - 1);
        int highestInnovation2 = tmpList2.isEmpty() ? 0 : tmpList2.get(tmpList2.size() - 1);

        int indices = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            Node node1 = genome1.getNodes().get(i);
            Node node2 = genome2.getNodes().get(i);
            
            if (node1 != null) {
                if (node2 == null) {
                    // Only node1 exists; determine if it's disjoint or excess
                    if (highestInnovation2 > i) {
                        disjointGenes++;
                    } else {
                        excessGenes++;
                    }
                }
            } else if (node2 != null) {
                // Only node2 exists; determine if it's disjoint or excess
                if (highestInnovation1 > i) {
                    disjointGenes++;
                } else {
                    excessGenes++;
                }
            }            
        }

        // Repeat the same process for the connections
        tmpList1 = genome1.getConnections().keySet().stream().sorted().toList();
        tmpList2 = genome2.getConnections().keySet().stream().sorted().toList();

        highestInnovation1 = tmpList1.get(tmpList1.size() - 1);
        highestInnovation2 = tmpList2.get(tmpList2.size() - 1);

        indices = Math.max(highestInnovation1, highestInnovation2);

        for (int i = 0; i <= indices; i++) {
            Connection connection1 = genome1.getConnections().get(i);
            Connection connection2 = genome2.getConnections().get(i);

            if (connection1 != null) {
                if (connection2 != null) {
                    // Both connections exist: matching genes
                    matchingGenes++;
                    weightDifference += Math.abs(connection1.getWeight() - connection2.getWeight());
                } else if (highestInnovation2 < i) {
                    // Only connection1 exists; check highestInnovation2 to distinguish excess from disjoint
                    excessGenes++;
                } else {
                    disjointGenes++;
                }
            } else if (connection2 != null) {
                // Only connection2 exists; check highestInnovation1 to distinguish excess from disjoint
                if (highestInnovation1 < i) {
                    excessGenes++;
                } else {
                    disjointGenes++;
                }
            }
        }   

        double averageWeightDifference = matchingGenes == 0 ? 0 : weightDifference / matchingGenes;

        return  excessGenes * C1 / N + disjointGenes * C2 / N + averageWeightDifference * C3;
    }

    /**
     * Evaluate the fitness of the genome
     * 
     * The fitness is evaluated by running the genome on the game for a number of simulations
     * The fitness is the number of wins out of the total number of simulations
     * 
     * @param game The game to evaluate the genome on, the opponent must be set as player 1
     * @return The fitness of the genome
     */
    public double evaluate(GameLoop game) {

        int winCount = 0;
        for (int i = 0; i < SIMULATION_NUMBER; i++) {
            if (game.run() == 1) {
                winCount++;
            }
        }

        game.reset();

        return winCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes.values()) {
            sb.append(node.toString()).append(";");
        }
        if (!nodes.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("£");
        for (Connection connection : connections.values()) {
            sb.append(connection.toString()).append(";");
        }
        if (!connections.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Genome parse(String json) {
        Genome genome = new Genome();

        // Remove the outer brackets and split the string into nodes and connections
        String[] parts = json.split("£");
        String nodesString = parts[0]; 
        // Parse nodes
        if (!nodesString.isEmpty()) {
            String[] nodeStrings = nodesString.split(";");
            for (String nodeString : nodeStrings) {
            Node node = Node.parse(nodeString);
            genome.addNode(node);
            }
        }

        if (parts.length > 1) {
            String connectionsString = parts[1];
            // Parse connections
            if (!connectionsString.isEmpty()) {
                String[] connectionStrings = connectionsString.split(";");
                for (String connectionString : connectionStrings) {
                    if (!connectionString.isEmpty()) {
                        Connection connection = Connection.parse(connectionString);
                        genome.addConnection(connection);
                    }
                }
            }
        }

        return genome;
    }
    
}
