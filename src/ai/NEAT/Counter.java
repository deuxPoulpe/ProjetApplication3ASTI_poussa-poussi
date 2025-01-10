package ai.NEAT;

public class Counter {

    private int count = 0;   

    public int nextNumber() {
        return count++;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
}
