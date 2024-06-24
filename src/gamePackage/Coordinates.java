package gamePackage;



public class Coordinates {

    private int x;
    private int y;

    public Coordinates(int myX, int myY) {
        this.x = myX;
        this.y = myY;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int myX) {
        this.x = myX;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int myY) {
        this.y = myY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Coordinates coord = (Coordinates) obj;
        return this.x == coord.getX() && this.y == coord.getY();
    }

    @Override
    public int hashCode() {
        return this.x * 31 + this.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
    
    public Coordinates clone() {
        return new Coordinates(this.x, this.y);
    }
}
