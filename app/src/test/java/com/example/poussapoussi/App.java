import gamePackage.*;


public class App {
    public static void main(String[] args) throws Exception {
        Grid grid = new Grid();

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true, false);

        Game game = new Game(grid);

        game.start();
        game.run();    
        
    }
}
