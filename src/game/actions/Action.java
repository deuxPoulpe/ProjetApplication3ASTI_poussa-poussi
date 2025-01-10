package actions;

public class Action {

    private Removal startRemoval;
    private Placement placement;
    private Push push;
    private Removal endRemoval;

    public Action() {
    }

    public Action(Placement placement, Push push) {
        this.placement = placement;
        this.push = push;
    }

    public Action(Removal startRemoval, Placement placement, Push push, Removal endRemoval) {
        this.startRemoval = startRemoval;
        this.placement = placement;
        this.push = push;
        this.endRemoval = endRemoval;
    }

    public Removal getStartRemoval() {
        return startRemoval;
    }

    public void setStartRemoval(Removal startRemoval) {
        this.startRemoval = startRemoval;
    }

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public Push getPush() {
        return push;
    }

    public void setPush(Push push) {
        this.push = push;
    }

    public Removal getEndRemoval() {
        return endRemoval;
    }

    public void setEndRemoval(Removal endRemoval) {
        this.endRemoval = endRemoval;
    }

    public String toString() {
        String startRemovalString = startRemoval == null ? "none" : startRemoval.toString();
        String placementString = placement == null ? "none" : placement.toString();
        String pushString = push == null ? "none" : push.toString();
        String endRemovalString = endRemoval == null ? "none" : endRemoval.toString();

        return "Action : \n Start Removal: " + startRemovalString + "\n Placement: " + placementString + "\n Push: " + pushString + "\n End Removal: " + endRemovalString;
    }
    
}
