package gamePackage;

import java.util.Set;

public class RemovAction {

    private Set<Coordinates> coordinates;
    private Grid grid;

    public RemovAction(Set<Coordinates> coordinates, Grid grid) {
        this.coordinates = coordinates;
        this.grid = grid;
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setCoordinates(Set<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public String toString() {
        return "Coordinates: " + coordinates + "\n" + "Grid: " + grid;
    }
}