package agents;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

import actions.Removal;
import actions.Placement;
import actions.Push;
import game.Board;

public class Player extends Agent {
    Scanner scanner = new Scanner(System.in);
        
        public Player(int player) {
            super(player);
        }
    
        @Override
        public void play(Board board) {

            Removal startRemoval = inputRemoval(board);
            if (startRemoval != null) {
                board.remove(startRemoval);
            }

            System.out.println(board);

            Placement placement = inputPlacement(board);
            if (placement != null) {
                board.place(placement, player);
            }
            
            System.out.println(board);

            Push push = inputPush(board);
            if (push != null) {
                board.findPushDistance(push);
                board.push(push);
            }

        }

        public Removal inputRemoval(Board board) {

            // For each alignment of 5 pieces for the current player
            for (List<int[]> alignment : board.getAlignments(player, 5)) {
                
                // Remove two pieces from the alignment
                while (true) {
                    // Choose two pieces to remove
                    int[] piece1 = inputCoordinates("Enter the coordinates of the first piece to remove:");
                    int[] piece2 = inputCoordinates("Enter the coordinates of the second piece to remove:");
                    if (alignment.contains(piece1) && alignment.contains(piece2) && !piece1.equals(piece2)) {
                        return new Removal(new HashSet<>(Arrays.asList(piece1, piece2)));                    
                    }
                    else {
                        System.out.println("Invalid removal. Please try again.");
                    }
                }
            }
        return null;
        }

        public Placement inputPlacement(Board board) {
            if (board.isFull())
                return null;

            while(true) {
                int[] coordinates = inputCoordinates("Enter the coordinates of the piece to place:");
                if (board.isValidPlacement(coordinates[0], coordinates[1])) {
                    return new Placement(coordinates);
                }
                else {
                    System.out.println("Invalid placement. Please try again.");
                }
            }
        }

        public Push inputPush(Board board) {
            if (board.isFull())
                return null;

            while(true) {
                int[] coordinates = inputCoordinates("Enter the coordinates of the piece to push:");
                if (board.getPiece(coordinates[0], coordinates[1]) != player) {
                    System.out.println("Invalid push. Please try again.");
                    continue;
                }
                int[] direction = inputDirection("Enter the direction to push the piece (N, S, E, W):");
                Push push = new Push(coordinates, direction);
                if (direction != null && board.findPushDistance(push) > 0) {
                    return push;
                }
                else {
                    System.out.println("Invalid push. Please try again.");
                }
            }
        }

        public int[] inputCoordinates(String message) {
            System.out.println(message);
            String input = scanner.next();
        
            // Extraire les coordonnées x et y de la chaîne
            int x = input.charAt(1) - 'A';
            int y = Character.getNumericValue(input.charAt(0)) - 1;
        
            return new int[]{x, y};
        }

        public int[] inputDirection(String message) {
            System.out.println(message);
            char direction = scanner.next().charAt(0);
            switch (direction) {
                case 'N':
                    return new int[]{-1, 0};
                case 'S':
                    return new int[]{1, 0};
                case 'E':
                    return new int[]{0, 1};
                case 'W':
                    return new int[]{0, -1};
                default:
                    return null;
            }
        }
    
}
