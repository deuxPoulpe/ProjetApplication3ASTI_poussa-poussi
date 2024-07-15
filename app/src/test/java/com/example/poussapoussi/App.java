package com.example.poussapoussi;

import java.util.Scanner;

import gamePackage.*;



public class App {
    public static void main(String[] args) throws Exception {
        Grid grid = new Grid();
        final Scanner scanner = new Scanner(System.in);

        // Initialisation des paramètres de jeu
        Settings.getInstance(true, true, false);

        Game game = new Game(grid);
        char choice;
        while ((choice = scanner.next().charAt(0)) != '1' && choice != '2' && choice != '3') {
            System.out.println("Invalid choice. Please enter 1, 2 or 3.");
        }


        game.start(choice);
        game.run();    
        
    }
}
