package ai.NEAT;

public class Node implements Cloneable {

    enum Type {
        INPUT, OUTPUT, HIDDEN
    }

    private Type type;
    private int id;

    public Node(Type type, int id) {
        this.type = type;
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    @Override
    public Node clone() {
        try {
            return (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Node(type, id);
        }
    }

    @Override
    public String toString() {
        return type + "," + id;
    }

    public static Node parse(String s) {
        String[] parts = s.split(",");
        Type type = Type.valueOf(parts[0]);
        int id = Integer.parseInt(parts[1]);
        return new Node(type, id);
    }
}
