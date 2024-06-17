package myPackage;
import myPackage.Coordinates;
import myPackage.Token;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


public class Grid {

    private int size = 8;
    private HashMap<Coordinates, Token> grid;
    private HashMap<Coordinates, Token> tokensMoveStart = new HashMap<Coordinates, Token>();

    // Getters and Setters

    public HashMap<Coordinates, Token> getGrid() {
        return this.grid;
    }

    public void setGrid(HashMap<Coordinates, Token> grid) {
        this.grid = grid;
    }

    public int getSize() {
        return this.size;
    }

    public Token getToken(Coordinates coordinates) {
        return this.grid.get(coordinates);
    }

    // Constructors
    
    public Grid() {
        this.grid = new HashMap<Coordinates, Token>();
    }

    // Methods

    public void placeToken(char colour, Coordinates coordinates) {
        // Check if there is already a token at the given coordinates
        if (this.grid.containsKey(coordinates)) {
            throw new IllegalArgumentException("There is already a token at the given coordinates");
        }

        if (!hasNeighbours(coordinates) && ( coordinates.getX() != 0 && coordinates.getX() != 7 && coordinates.getY() != 0 && coordinates.getY() != 7))
        {
            throw new IllegalArgumentException("There is no neighbour at the given coordinates");
        }
        
        this.grid.put(coordinates, new Token(colour));
    }

    public void removeToken(Coordinates coordinates) {
        this.grid.remove(coordinates);
    }


    /**
     * Renvoie les coefficients de déplacement en x et en y pour une direction donnée.
     * 
     * @param direction la direction pour laquelle obtenir les coefficients de déplacement.
     * @throws IllegalArgumentException si la direction n'est pas U, D, R ou L.
     * @return les coefficients de déplacement en x et en y pour la direction donnée.
     */
    public ArrayList<Integer> getCoeffs(char direction) {

        if (direction != 'U' && direction != 'D' && direction != 'R' && direction != 'L') {
            throw new IllegalArgumentException("Direction must be U, D, R or L");
        }
        
        ArrayList<Integer> coeffs = new ArrayList<Integer>();
        switch (direction) {
            case 'U': coeffs.add(0); coeffs.add(-1); break;
            case 'D': coeffs.add(0); coeffs.add(1); break;
            case 'R': coeffs.add(1); coeffs.add(0); break;
            case 'L': coeffs.add(-1); coeffs.add(0); break;
        }
        return coeffs;
    }

    /**
     * Renvoie le nombre de cellules vides dans une direction donnée à partir de coordonnées données.
     * @param lastToken les coordonnées à partir desquelles commencer à chercher les cellules vides.
     * @param coeffX le coefficient de déplacement en x.
     * @param coeffY le coefficient de déplacement en y.
     * @return le nombre de cellules vides dans la direction donnée.
     */
    public int getNbEmptyCellsInDirection(Coordinates lastToken, int coeffX, int coeffY) {
        int nbEmptyCells = 0;
        Coordinates currentCoordinates = new Coordinates(lastToken.getX() + coeffX, lastToken.getY() + coeffY);
        while (!this.grid.containsKey(currentCoordinates) &&
         (currentCoordinates.getX() >= 0 && currentCoordinates.getX() <= 7 && currentCoordinates.getY() >= 0 && currentCoordinates.getY() <= 7)){
            currentCoordinates.setX(currentCoordinates.getX() + coeffX);
            currentCoordinates.setY(currentCoordinates.getY() + coeffY);
            nbEmptyCells++; 
        }
        if (nbEmptyCells == 0) {
            throw new IllegalArgumentException("There is no empty cell in this direction");
        }
        return nbEmptyCells;
    }
    
    
    /**
     * Renvoie les jetons à déplacer dans une direction donnée à partir de coordonnées données.
     * @param coordinate les coordonnées à partir desquelles commencer à chercher les jetons à déplacer.
     * @param directions la direction dans laquelle chercher les jetons à déplacer.
     * @throws IllegalArgumentException si les coordonnées ne contiennent pas de jeton ou si la direction n'est pas U, D, R ou L.
     * @return les jetons à déplacer dans la direction donnée.
     */
    public HashMap<Coordinates, Token> getTokensToMove(Coordinates coordinate, int coeffX, int coeffY) {
        

        if (!this.grid.containsKey(coordinate)) {
            throw new IllegalArgumentException("There is no token at the given coordinates");
        }


        HashMap<Coordinates, Token> tokensToMove = new HashMap<Coordinates, Token>();
        Coordinates iterationsCoordinates = new Coordinates(coordinate.getX(), coordinate.getY());
        
        while (this.grid.containsKey(iterationsCoordinates)) {
            tokensToMove.put(new Coordinates(iterationsCoordinates.getX(), iterationsCoordinates.getY()), this.grid.get(iterationsCoordinates));
            iterationsCoordinates.setX(iterationsCoordinates.getX() + coeffX);
            iterationsCoordinates.setY(iterationsCoordinates.getY() + coeffY);
        }
        
        return tokensToMove;
    }


    // public void removeTowTokens(HashMap<Coordinates, Token> fiveTokens, Coordinates firstCoordinates, Coordinates secondCoordinates) {
    //     if (!fiveTokens.containsKey(firstCoordinates)) {
    //         String message = "There is no token at the given coordinates : " + firstCoordinates.toString();
    //         throw new IllegalArgumentException(message);
    //     }
    //     if (!fiveTokens.containsKey(secondCoordinates)) {
    //         String message = "There is no token at the given coordinates : " + secondCoordinates.toString();
    //         throw new IllegalArgumentException(message);
    //     }        
    // }



    /**
     * Déplace un jeton dans une direction donnée.
     * @param colour la couleur du jeton à déplacer.
     * @param coordinates les coordonnées du jeton à déplacer.
     * @param direction la direction dans laquelle déplacer le jeton.
     * @throws IllegalArgumentException si les coordonnées ne contiennent pas de jeton, si la direction n'est pas U, D, R ou L ou si le jeton à déplacer n'est pas de la couleur donnée.
     * 
     */
    public void pushToken(char colour, Coordinates coordinates, char direction){
        
        if (!this.grid.containsKey(coordinates)) {
            throw new IllegalArgumentException("There is no token at the given coordinates");
        }

        if (this.grid.get(coordinates).getColour() != colour) {
            throw new IllegalArgumentException("You can only push your own tokens");
        }

        int coeffX = getCoeffs(direction).get(0);
        int coeffY = getCoeffs(direction).get(1);

        HashMap<Coordinates, Token> tokensToMove = getTokensToMove(coordinates, coeffX, coeffY);

        Coordinates lastToken = new Coordinates(coordinates.getX() + (tokensToMove.size() - 1) * coeffX, coordinates.getY() + (tokensToMove.size() - 1) * coeffY);
        int nbEmptyCells = getNbEmptyCellsInDirection(lastToken, coeffX, coeffY);

        
        // remove the tokens to move from the grid
        
        // place the tokens to move in the new coordinates

        HashMap<Coordinates, Token> tokensMoveEnd = new HashMap<Coordinates, Token>();

        for (Coordinates c : tokensToMove.keySet()) {

            tokensMoveEnd.put(new Coordinates(c.getX() + nbEmptyCells * coeffX, c.getY() + nbEmptyCells * coeffY), tokensToMove.get(c));
            
        }

        if (tokensMoveStart.equals(tokensMoveEnd)) {
            throw new IllegalArgumentException("You are trying to push tokens in the same previous position");
        }
        
        for (Coordinates c : tokensToMove.keySet()) {
            this.grid.remove(c);
            }

        for (Coordinates c : tokensToMove.keySet()) {
            char tokenColour = tokensToMove.get(c).getColour();
            placeToken(tokenColour, new Coordinates(c.getX() + nbEmptyCells * coeffX, c.getY() + nbEmptyCells * coeffY));
            }

        this.tokensMoveStart = tokensToMove;


        
    }
    
    public List<List<List<Coordinates>>> getAlignmentsOfFive() {

        List<List<List<Coordinates>>> result = new ArrayList<>();
        result.add(new ArrayList<List<Coordinates>>()); // blue tokens
        result.add(new ArrayList<List<Coordinates>>()); // yellow tokens

        int [][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        // for each token in the grid
        for (Coordinates c : this.grid.keySet()) {
            Token token = this.grid.get(c);
            char colour = token.getColour();
            
            // for each direction
            for (int[] direction : directions) {

                // if the token is already aligned in this direction, skip
                if (!token.getAlignments().contains(direction)) {

                    // if there are 5 tokens in a row in this direction
                    Set<Coordinates> visited = new HashSet<>();
                    List<Coordinates> neighbours = getNeighboursInDirection(c, direction, colour, visited);
                    if (neighbours.size() >= 5) {
                        
                        // set the aligment of the tokens to the direction
                        for (Coordinates neighbourCoordinates : neighbours) {
                            getToken(neighbourCoordinates).addToAlignments(direction);
                        }

                        // add the tokens to the result
                        if (colour == 'B') {
                            result.get(0).add(neighbours);
                        } else if (colour == 'Y') {
                            result.get(1).add(neighbours);
                        }

                        // print the alignment
                        System.out.println("Alignment of 5 !");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Renvoie les jetons voisins d'un jeton donné dans une direction donnée.
     * @param coordinates les coordonnées du jeton à partir duquel chercher les voisins.
     * @param direction la direction dans laquelle chercher les voisins.
     * @param colour la couleur des jetons voisins à chercher.
     * @return une liste des jetons voisins dans la direction donnée.
     */
    public List<Coordinates> getNeighboursInDirection(Coordinates coordinates, int direction[], char colour, Set<Coordinates> visited) {
        List<Coordinates> neighbours = new ArrayList<>();
        Coordinates neighbour1 = new Coordinates(coordinates.getX() + direction[0], coordinates.getY() + direction[1]);
        Coordinates neighbour2 = new Coordinates(coordinates.getX() - direction[0], coordinates.getY() - direction[1]);

        if (!visited.contains(neighbour1) && grid.get(neighbour1) != null && grid.get(neighbour1).getColour() == colour) {
            neighbours.add(neighbour1);
            visited.add(neighbour1);
            neighbours.addAll(getNeighboursInDirection(neighbour1, direction, colour, visited));
        }
        if (!visited.contains(neighbour2) && grid.get(neighbour2) != null && grid.get(neighbour2).getColour() == colour) {
            neighbours.add(neighbour2);
            visited.add(neighbour2);
            neighbours.addAll(getNeighboursInDirection(neighbour2, direction, colour, visited));
        }

        return neighbours;
    }

    public boolean hasNeighbours(Coordinates coordinates) {
        int [][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : directions)
        {
            Coordinates neighbour = new Coordinates(coordinates.getX()+direction[0], coordinates.getY()+direction[1]);
            if (this.grid.get(neighbour) != null)
            {
                return true;
            }
        }
        return false;
    }

    public void display() {
        System.out.print("x─");
        for (int i = 0; i < this.size - 1; i++) {
            System.out.print("──┬─");
        }
        System.out.println("──x");
        
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                System.out.print("│");
                Coordinates c = new Coordinates(j, i);
                if (this.grid.containsKey(c)) {
                    if (this.grid.get(c).getColour() == 'B') {
                        System.out.print("\u001B[34m");

                        System.out.print("███");
                    } else {
                        System.out.print("\u001B[33m");
                        System.out.print("███");
                    }
                    System.out.print("\u001B[0m");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println("│");
            
            if (i < this.size - 1) {
                System.out.print("├─");
                for (int j = 0; j < this.size - 1; j++) {
                    System.out.print("──┼─");
                }
                System.out.println("──┤");
            }
        }
        System.out.print("x─");
        for (int i = 0; i < this.size - 1; i++) {
            System.out.print("──┴─");
        }
        System.out.println("──x");
    }
        
}