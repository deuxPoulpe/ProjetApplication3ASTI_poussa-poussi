package myPackage;

import java.util.List;

public class GridTree {

    private final GridTree parent;
    private List<GridTree> children;
    private Grid grid;

    public GridTree(GridTree parent, Grid grid) {
        this.parent = parent;
        this.grid = grid;
    }

    public Grid getGrid() {
        return grid;
    }

    public GridTree getParent() {
        return parent;
    }

    public List<GridTree> getChildren() {
        return children;
    }

    public void appendChild(GridTree child) {
        children.add(child);
    }
}
