package actions;

public class Push {

    private int[] coordinates;
    private int[] direction;
    private int distance;

    public Push() {
        this.coordinates = new int[2];
        this.direction = new int[2];
    }
    
    public Push(int[] coordinates, int[] direction) {
        this.coordinates = coordinates;
        this.direction = direction;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getDirection() {
        return direction;
    }

    public void setDirection(int[] direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String toString() {
        return "Push: " + coordinates[0] + ", " + coordinates[1] + " in direction " + direction[0] + ", " + direction[1];
    }

    @Override
    public Push clone() {
        return new Push(coordinates.clone(), direction.clone());
    }
}
