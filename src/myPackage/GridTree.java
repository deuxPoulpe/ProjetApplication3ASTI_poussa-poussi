package myPackage;

import java.util.List;

public class GridTree {

    private final GridTree parent;
    private List<GridTree> children;
    private Grid grid;
    private Coordinates placeCoordinates;
    private PushAction pushAction;

    public GridTree(GridTree parent, Grid grid, Coordinates placeCoordinates, PushAction pushAction) {
        this.parent = parent;
        this.grid = grid;
    }

    public Grid getGrid() {
        return grid;
    }

    public Coordinates getPlaceCoordinates() {
        return placeCoordinates;
    }

    public PushAction getPushAction() {
        return pushAction;
    }

    public GridTree getParent() {
        return parent;
    }

    public List<GridTree> getChildren() {
        return children;
    }

    public void addChild(GridTree child) {
        children.add(child);
    }
}
