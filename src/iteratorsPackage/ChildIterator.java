package iteratorsPackage;

import gamePackage.GridTree;

public class ChildIterator {

    private GridTree currentNode;
    private PlaceChildIterator placeChildIterator;
    private PushChildIterator pushChildIterator;
    
    public ChildIterator(GridTree myNode) {
        this.currentNode = myNode;
        this.placeChildIterator = new PlaceChildIterator(myNode);
        this.pushChildIterator = new PushChildIterator(myNode);
    }

    public boolean hasNext() {
        // Si on est en phase de placement
        if (currentNode.getPushAction() == null) {
            return placeChildIterator.hasNext();
        }
        return pushChildIterator.hasNext();
    }

    public GridTree next() {
        // Si on est en phase de placement
        if (currentNode.getPushAction() == null) {
            currentNode = placeChildIterator.next();
        }
        // Si on est en phase de poussée
        return pushChildIterator.next();
    }
}
