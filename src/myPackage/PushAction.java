package myPackage;

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
    
}
