package gamePackage;

public class PushAction {

    Coordinates coordinates;
    private int[] direction;

    public PushAction(Coordinates coordinates, int[] direction) {
        this.coordinates = coordinates;
        this.direction = direction;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int[] getDirection() {
        return direction;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setDirection(int[] direction) {
        this.direction = direction;
    }

    public String toString() {
        String strCoordinates = "Coordinates: " + coordinates + "\n";
        Coordinates directionCoordinates = new Coordinates(direction[0], direction[1]);
        String strDirection = "Direction: " + directionCoordinates  + "\n";
        return strCoordinates + strDirection;
    }
}
