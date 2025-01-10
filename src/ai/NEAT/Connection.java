package ai.NEAT;

public class Connection implements Cloneable{
    private int inNode;
    private int outNode;
    private double weight;
    private boolean enabled;
    private int id;

    public Connection(int inNode, int outNode, double weight, boolean enabled, int id) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.enabled = enabled;
        this.id = id;
    }

    public int getInNode() {
        return inNode;
    }

    public int getOutNode() {
        return outNode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }   

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Connection clone() {
        try {
            return (Connection) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Connection(inNode, outNode, weight, enabled, id);
        }
    }

    @Override
    public String toString() {
        return inNode + "," + outNode + "," + weight + "," + enabled + "," + id;
    }

    public static Connection parse(String s) {
        String[] parts = s.split(",");
        int inNode = Integer.parseInt(parts[0]);
        int outNode = Integer.parseInt(parts[1]);
        double weight = Double.parseDouble(parts[2]);
        boolean enabled = Boolean.parseBoolean(parts[3]);
        int id = Integer.parseInt(parts[4]);
        return new Connection(inNode, outNode, weight, enabled, id);
    }
}
