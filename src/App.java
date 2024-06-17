import myPackage.*;


public class App {
    public static void main(String[] args) throws Exception {
        Grid grid = new Grid();
        grid.placeToken('B', new Coordinates(0, 0));
        grid.placeToken('B', new Coordinates(0, 1));
        grid.placeToken('B', new Coordinates(0, 2));
        grid.placeToken('B', new Coordinates(0, 3));
        grid.placeToken('B', new Coordinates(0, 4));

        Game game = new Game(grid);
        game.run();
        
        
    }
}
