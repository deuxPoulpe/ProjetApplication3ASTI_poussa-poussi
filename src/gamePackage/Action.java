package gamePackage;

import java.util.Set;

public class Action {
    
    private Set<Coordinates> startRemove;
    private Coordinates placement;
    private PushAction push;
    private Set<Coordinates> endRemove;
    private Grid grid;

    public Action(Set<Coordinates> startRemove, Coordinates placement, PushAction push, Set<Coordinates> endRemove, Grid myGrid) {
        this.startRemove = startRemove;
        this.placement = placement;
        this.push = push;
        this.endRemove = endRemove;
        this.grid = myGrid;
    }

    public Set<Coordinates> getStartRemove() {
        return startRemove;
    }

    public Coordinates getPlacement() {
        return placement;
    }

    public PushAction getPush() {
        return push;
    }

    public Set<Coordinates> getEndRemove() {
        return endRemove;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setStartRemove(Set<Coordinates> startRemove) {
        this.startRemove = startRemove;
    }

    public void setPlacement(Coordinates placement) {
        this.placement = placement;
    }

    public void setPush(PushAction push) {
        this.push = push;
    }

    public void setEndRemove(Set<Coordinates> endRemove) {
        this.endRemove = endRemove;
    }

    public void setGrid(Grid actionGrid) {
        this.grid = actionGrid;
    }

    @Override
    public String toString() {
        String strStartRemove = "Start remove: " + startRemove + "\n";
        String strPlacement = "Placement: " + placement + "\n";
        String strPush = "Push: " + push + "\n";
        String strEndRemove = "End remove: " + endRemove + "\n";
        return strStartRemove + strPlacement + strPush + strEndRemove;
    }

    @Override
    public Action clone() {
        PushAction pushClone;
        pushClone = push == null ? null : push.clone();
        return new Action(startRemove, placement, pushClone, endRemove, grid.clone());
    }
    
}
