import myPackage.*;


public class App {
    public static void main(String[] args) throws Exception {
        Grid grid = new Grid();
        // grid.placeToken('B', new Coordinates(0, 0));
        // grid.placeToken('B', new Coordinates(0, 1));
        // grid.placeToken('B', new Coordinates(0, 2));
        // grid.placeToken('B', new Coordinates(0, 3));
        // grid.placeToken('B', new Coordinates(0, 4));

        // grid.placeToken('Y', new Coordinates(1, 0));
        // grid.placeToken('Y', new Coordinates(1, 1));
        // grid.placeToken('Y', new Coordinates(1, 2));
        // grid.placeToken('Y', new Coordinates(1, 3));
        // grid.placeToken('Y', new Coordinates(1, 4));

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, false);

        Game game = new Game(grid);

        game.start();
        game.run();    
        
    }
}
