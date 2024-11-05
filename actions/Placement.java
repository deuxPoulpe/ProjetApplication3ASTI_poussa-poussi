package actions;

public class Placement {

    private int[] coordinates;

    public Placement(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int getX() {
        return coordinates[0];
    }

    public int getY() {
        return coordinates[1];
    }

    public String toString() {
        return "Placement: " + coordinates[0] + ", " + coordinates[1];
    }

    @Override
    public Placement clone() {
        return new Placement(coordinates.clone());
    }
    
}
