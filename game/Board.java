package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import actions.Placement;
import actions.Push;
import actions.Removal;

public class Board {
    private int size;
    private int[][] grid;
    private int pieceCount = 0;

    public Board(int size) {
        this.size = size;
        this.grid = new int[size][size];
        // Initialiser la grille avec des zéros
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.grid[i][j] = 0;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getPiece(int x, int y) {
        return grid[x][y];
    }

    // Finds all diagonal, horizontal, and vertical alignments of a given player of a given size
    public List<List<int[]>> getAlignments(int player, int alignmentSize) {
        List<List<int[]>> alignments = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}}; // Directions: right, down, down-right, down-left

        // Iterate over each cell in the grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Check each direction from the current cell
                for (int[] direction : directions) {
                    List<int[]> alignment = new ArrayList<>();
                    boolean isAligned = true;
                    // Check for alignment of the specified size in the current direction
                    for (int k = 0; k < alignmentSize; k++) {
                        int x = i + k * direction[0];
                        int y = j + k * direction[1];
                        // Check if the cell is within bounds and belongs to the player
                        if (x >= 0 && x < size && y >= 0 && y < size && grid[x][y] == player) {
                            alignment.add(new int[]{x, y});
                        } else {
                            isAligned = false;
                            break;
                        }
                    }
                    // If an alignment is found, add it to the list
                    if (isAligned) {
                        alignments.add(alignment);
                    }
                }
            }
        }

        return alignments;
    }

    public void place(Placement placement, int player) {
        int[] coordinates = placement.getCoordinates();
        grid[coordinates[0]][coordinates[1]] = player;
        pieceCount++;
    }

    public void push(Push push){
        int distance = push.getDistance();
        int[] direction = push.getDirection();
        int[] coordinates = push.getCoordinates();
        int dx = direction[0];
        int dy = direction[1];
        int x = coordinates[0] + (distance + 1) * dx;
        int y = coordinates[1] + (distance + 1) * dy;
        int i = 1;

        // Iterate over the cells in the direction until a piece is found
        while(x >= 0 && x < size && y >= 0 && y < size && grid[x][y] == 0){
            x += dx;
            y += dy;
            i++;
        }

        // Move the pieces
        for (int j = 0; j < distance; j++) {
            int newX = x - j * dx;
            int newY = y - j * dy;
            int previousX = newX - i * dx;
            int previousY = newY - i * dy;
            System.out.println("Moving piece from " + previousX + ", " + previousY + " to " + newX + ", " + newY);
            grid[newX][newY] = grid[previousX][previousY];
            grid[previousX][previousY] = 0;
        }
    }

    public void remove(Removal removal) {
        for (int[] coordinates : removal.getCombination()) {
            grid[coordinates[0]][coordinates[1]] = 0;
            pieceCount--;
        }
    }

    public boolean isValidPlacement(int x, int y) {

        // Return false if the cell is not empty
        if (grid[x][y] != 0) {
            return false;
        }

        // Return false if the cell is out of bounds
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return false;
        }

        // Return true if the cell is on the edge of the board
        if  (x == 0 || x == size - 1 || y == 0 || y == size - 1){
            return true;
        }

        // Return true if the cell has a neighbor
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        for (int[] direction : directions) {
            if (grid[x + direction[0]][y + direction[1]] != 0) {
                return true;
            }
        }

        return false;
    }

    public int findPushDistance(Push push) {
        int[] direction = push.getDirection();
        int[] coordinates = push.getCoordinates();
        int x = coordinates[0];
        int y = coordinates[1];

        // Iterate over the cells in the direction of the push until an empty cell is found
        int i = 1;
        while (x + i * direction[0] >= 0 && x + i * direction[0] < size && y + i * direction[1] >= 0 && y + i * direction[1] < size) {
            if (grid[x + i * direction[0]][y + i * direction[1]] == 0) {
                push.setDistance(i - 1);
                return i;
            }
            i++;
        }
        // Return 0 if there is no room to push the piece
        return 0;
    }

    public boolean isFull() {
        return pieceCount == size * size;
    }
    
    // Generate a shuffled list of all cells on the board
    public List<int[]> generateShuffledCells() {
        List<int[]> allCells = new ArrayList<>();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                allCells.add(new int[]{x, y});
            }
        }
        Collections.shuffle(allCells);
        return allCells;
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();

        // Append column indices
        for (int i = 0; i < this.size; i++) {
            boardString.append(i == 0 ? "   " + i : "   " + i);
        }
        boardString.append("\n");

        // Append top border
        boardString.append(" ┌─");
        for (int i = 0; i < this.size - 1; i++) {
            boardString.append("──┬─");
        }
        boardString.append("──┐\n");

        // Append rows
        for (int i = 0; i < this.size; i++) {
            boardString.append(i).append("│");
            for (int j = 0; j < this.size; j++) {
                if (grid[i][j] == 1) {
                    boardString.append("\u001B[34m███\u001B[0m"); // Blue token
                } else if (grid[i][j] == 2) {
                    boardString.append("\u001B[33m███\u001B[0m"); // Yellow token
                } else {
                    boardString.append("   ");
                }
                boardString.append("│");
            }
            boardString.append("\n");

            // Append row separator
            if (i < this.size - 1) {
                boardString.append(" ├─");
                for (int j = 0; j < this.size - 1; j++) {
                    boardString.append("──┼─");
                }
                boardString.append("──┤\n");
            }
        }

        // Append bottom border
        boardString.append(" └─");
        for (int i = 0; i < this.size - 1; i++) {
            boardString.append("──┴─");
        }
        boardString.append("──┘\n");

        return boardString.toString();
    }

    @Override
    public Board clone() {
        Board newBoard = new Board(this.size);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                newBoard.grid[i][j] = this.grid[i][j];
            }
        }
        return newBoard;
    }
}