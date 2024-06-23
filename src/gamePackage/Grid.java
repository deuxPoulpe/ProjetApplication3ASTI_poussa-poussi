package gamePackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import treeFormationPackage.EmptyCoordIterator;



public class Grid {

    private final int size = 8;
    private HashMap<Coordinates, Token> map;
    private HashMap<Coordinates, Token> previousMovedTokens = new HashMap<>();



    public HashMap<Coordinates, Token> getHashMap() {
        return this.map;
    }

    public void setGrid(HashMap<Coordinates, Token> map) {
        this.map = map;
    }

    public int getSize() {
        return this.size;
    }

    public Token getToken(Coordinates coordinates) {
        return this.map.get(coordinates);
    }

    public HashMap<Coordinates, Token> getmovedTokens() {
        return this.previousMovedTokens;
    }

    // Constructors
    
    public Grid(HashMap<Coordinates, Token> map) {
        this.map = map;
    }

    public Grid() {
        this.map = new HashMap<>();
    }

    // Methods

    public Grid clone() {
        HashMap<Coordinates, Token> newGrid = new HashMap<>();
        for (Coordinates c : map.keySet()) {
            newGrid.put(c, map.get(c));
        }
        return new Grid(newGrid);
    }

    public boolean isFull() {
        return map.size() == this.size * this.size;
    }


    public void placeToken(char color, Coordinates coordinates) {
        // Check if there is already a token at the given coordinates
        if (map.containsKey(coordinates)) {
            throw new IllegalArgumentException("Attempted to place at" + coordinates + " but there is already a token there");
        }

        if (!hasNeighbours(coordinates) && ( coordinates.getX() != 0 && coordinates.getX() != size - 1 && coordinates.getY() != 0 && coordinates.getY() != size - 1))
        {
            throw new IllegalArgumentException("There is no neighbour at the given coordinates");
        }
        
        map.put(coordinates, new Token(color));
    }

    public void removeToken(Coordinates coordinates) {
        map.remove(coordinates);
    }

    public void removeTokens(Collection<Coordinates> tokens) {
        for (Coordinates c : tokens) {
            removeToken(c);
        }
    }

    /**
     * Renvoie le nombre de cellules vides dans une direction donnée à partir de coordonnées données.
     * @param lastToken les coordonnées à partir desquelles commencer à chercher les cellules vides.
     * @param coeffX le coefficient de déplacement en x.
     * @param coeffY le coefficient de déplacement en y.
     * @return le nombre de cellules vides dans la direction donnée.
     */
    public int countEmptyCellsInDirection(Coordinates coordinates, int[] direction) {

        int nbEmptyCells = 0;
        Coordinates currentCoordinates = new Coordinates(coordinates.getX() + direction[0], coordinates.getY() + direction[1]);

        while (!map.containsKey(currentCoordinates) &&
            (currentCoordinates.getX() >= 0 && currentCoordinates.getX() <= 7 && currentCoordinates.getY() >= 0 && currentCoordinates.getY() <= 7)) {
            currentCoordinates = new Coordinates(currentCoordinates.getX() + direction[0], currentCoordinates.getY() + direction[1]);
            nbEmptyCells++; 
        }
        
        return nbEmptyCells;
    }
    
    public HashMap<Coordinates, Token> getTokensToMove(PushAction pushAction, char color) {

        Coordinates coordinates = pushAction.getCoordinates();
        int[] direction = pushAction.getDirection();

        // On vérifie si le jeton à déplacer existe et est de la bonne couleur
        if (!map.containsKey(coordinates) || map.get(coordinates) == null) {
            throw new IllegalArgumentException("There is no token at the given coordinates");
        }

        // On vérifie si le jeton à déplacer est de la bonne couleur
        else if (map.get(coordinates).getColor() != color) {
            throw new IllegalArgumentException("The token at the given coordinates is not of the right color");
        }

        HashMap<Coordinates, Token> tokensToMove = new HashMap<>();
        Coordinates iterationsCoordinates = new Coordinates(coordinates.getX(), coordinates.getY());
        
        // On récupère les jetons à déplacer
        while (map.containsKey(iterationsCoordinates)) {
            tokensToMove.put(new Coordinates(iterationsCoordinates.getX(), iterationsCoordinates.getY()), map.get(iterationsCoordinates));
            iterationsCoordinates = new Coordinates(iterationsCoordinates.getX() + direction[0], iterationsCoordinates.getY() + direction[1]);
        }
        
        return tokensToMove;
    }
    
    public HashMap<Coordinates, Token> getMovedTokens(PushAction pushAction, HashMap<Coordinates, Token> tokensToMove) {

        int[] direction = pushAction.getDirection();
        Coordinates coordinates = pushAction.getCoordinates();

        // Les coordonnées du dernier jeton à déplacer
        Coordinates lastToken = new Coordinates(coordinates.getX() + (tokensToMove.size() - 1) * direction[0], coordinates.getY() + (tokensToMove.size() - 1) * direction[1]); 

        // Calcul de la distance de déplacement
        int distance = countEmptyCellsInDirection(lastToken, direction);
        
        // On vérifie si le dernier jeton à déplacer est sur un bord du plateau
        if (distance == 0) {
            throw new IllegalArgumentException("There is no empty cell in this direction");
        }

        int [] movementVector = {distance * direction[0], distance * direction[1]};

        // On stocke les nouvelles coordonnées des jetons déplacés
        HashMap <Coordinates, Token> tokensMoved = new HashMap<>();
        for (Coordinates c : tokensToMove.keySet()) {
            Token token = tokensToMove.get(c);
            Coordinates newTokenCoordinates = new Coordinates(c.getX() + movementVector[0], c.getY() + movementVector[1]);
            tokensMoved.put(newTokenCoordinates, token);
        }

        // On vérifie si les jetons déplacés sont dans le plateau
        if (!Settings.getInstance().getAllowPushBack() && previousMovedTokens != null && previousMovedTokens.equals(tokensMoved)) {
            throw new IllegalArgumentException("You are trying to push tokens in the same previous position");
        }

        return tokensMoved;
    }

    public boolean isValidPushAction(PushAction pushAction, char color) {
        boolean isValid = false; // Utilisez une variable booléenne pour suivre la validité de l'action

        try {
            // Vérifie si le jeton à déplacer existe et est de la bonne couleur
            HashMap<Coordinates, Token> tokensToMove = getTokensToMove(pushAction, color);

            if (!Settings.getInstance().getAllowPushBack()) {
                // Si le joueur n'a pas le droit de pousser les jetons en arrière, on vérifie si les jetons déplacés sont valides
                HashMap<Coordinates, Token> movedTokens = getMovedTokens(pushAction, tokensToMove);
                isValid = movedTokens != null && !movedTokens.isEmpty(); // Vérifie si movedTokens n'est pas null et pas vide
            } else {
                // Sinon, on vérifie juste s'il y a de la place pour pousser les jetons
                Coordinates lastToken = new Coordinates(pushAction.getCoordinates().getX() + (tokensToMove.size() - 1) * pushAction.getDirection()[0], pushAction.getCoordinates().getY() + (tokensToMove.size() - 1) * pushAction.getDirection()[1]);
                int distance = countEmptyCellsInDirection(lastToken, pushAction.getDirection());
                isValid = distance > 0; // Vérifie si la distance est supérieure à 0
            }
        } catch (IllegalArgumentException e) {
            isValid = false; // En cas d'exception, l'action n'est pas valide
        }

        return isValid; // Retourne la validité de l'action
    }

    public void pushToken(PushAction pushAction, char color) {

        HashMap<Coordinates, Token> tokensToMove = getTokensToMove(pushAction, color);

        // On supprime les jetons à déplacer de la grille
        for (Coordinates c : tokensToMove.keySet()) {
            map.remove(c);
        }

        HashMap<Coordinates, Token> movedTokens = getMovedTokens(pushAction, tokensToMove);

        // On place les jetons à déplacer dans la nouvelle position
        for (Coordinates c : movedTokens.keySet()) {
            map.put(c, movedTokens.get(c));
        }

        // On met à jour les jetons déplacés
        previousMovedTokens = movedTokens;
    }
 
    /**
     * Renvoie les alignements de 5 jetons dans le plateau.
     * @return une liste des alignements de 5 jetons dans le plateau.
     * @param specifiedColor la couleur des jetons à aligner.
     * @param alignmentSize la taille de l'alignement à chercher.
     */
    public List<List<Coordinates>> getAlignments(char specifiedColor, int alignmentSize) {

        List<List<Coordinates>> result = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        Set<Coordinates> visitedTokens = new HashSet<>();

        // Pour chaque jeton dans la grille
        for (Coordinates c : map.keySet()) {
            Token token = map.get(c);
            char color = token.getColor();
        
            // Si le jeton n'est pas de la couleur spécifiée ou a déjà été visité, on passe au suivant
            if (color != specifiedColor || visitedTokens.contains(c)) {
                continue;
            }

            // Pour chaque direction
            for (int[] direction : directions) {

                // Si le jeton n'est pas déjà aligné dans cette direction
                if (!token.getAlignments().contains(direction)) {

                    // Trouver les voisins alignés dans cette direction
                    Set<Coordinates> visited = new HashSet<>();
                    List<Coordinates> neighbours = getAlignment(c, direction, visited);
                    visitedTokens.addAll(visited);
                    
                    // Si le nombre de voisins alignés est suffisant
                    if (neighbours.size() >= alignmentSize) {
                        // Marquer tous les jetons alignés et les ajouter aux résultats
                        for (Coordinates neighbourCoordinates : neighbours) {
                            getToken(neighbourCoordinates).addToAlignments(direction);
                            visitedTokens.add(neighbourCoordinates);
                        }
    
                        result.add(neighbours);
                    }
                }
            }
        }
    
        // Nettoyer les alignements des jetons avant de retourner le résultat
        for (List<Coordinates> alignment : result) {
            for (Coordinates c : alignment) {
                getToken(c).clearAlignments();
            }
        }
        
        return result;
    }
    
    /**
     * Renvoie les jetons voisins d'un jeton donné dans une direction donnée.
     * @param coordinates les coordonnées du jeton à partir duquel chercher les voisins.
     * @param direction la direction dans laquelle chercher les voisins.
     * @param color la couleur des jetons voisins à chercher.
     * @return une liste des jetons voisins dans la direction donnée.
     * 
     * Complexité: O(n) où n est le nombre de jetons dans la direction donnée.
     */
    public List<Coordinates> getAlignment(Coordinates coordinates, int direction[], Set<Coordinates> visited) {
            // Initialiser les structures de données
            List<Coordinates> neighbours = new ArrayList<>();
            Stack<Coordinates> stack = new Stack<>();
            
            // Commencer par les coordonnées initiales
            stack.push(coordinates);

            // Parcourir les coordonnées jusqu'à ce que la pile soit vide
            while (!stack.isEmpty()) {
                Coordinates current = stack.pop();

                // Parcourir les voisins de la coordonnée actuelle
                for (int i = -1; i <= 1; i += 2) {
                    Coordinates neighbour = new Coordinates(current.getX() + i * direction[0], current.getY() + i * direction[1]);

                    // Vérifier les conditions pour ajouter le voisin à la liste
                    if (!visited.contains(neighbour) && map.get(neighbour) != null && map.get(neighbour).getColor() == getToken(current).getColor()) {
                        neighbours.add(neighbour);
                        visited.add(neighbour);
                        stack.push(neighbour);
                    }
                }
            }

            // Retourner la liste des voisins
            return neighbours;
        }

    /**
     * Vérifie si une case a des voisins.
     * @param coordinates les coordonnées de la case à vérifier.
     * @return true si la case a des voisins, false sinon.
     */
    public boolean hasNeighbours(Coordinates coordinates) {
        int [][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : directions)
        {
            Coordinates neighbour = new Coordinates(coordinates.getX()+direction[0], coordinates.getY()+direction[1]);
            if (map.get(neighbour) != null)
            {
                return true;
            }
        }
        return false;
    }

    public void display() {
        for (int i = 0; i < this.size; i++) {
            System.out.print(i == 0 ? "   " + i : "   " + i);
        }
        System.out.println();

        System.out.print(" ┌─");
        for (int i = 0; i < this.size - 1; i++) {
            System.out.print("──┬─");
        }
        System.out.println("──┐");

        for (int i = 0; i < this.size; i++) {
            System.out.print(i + "│");
            for (int j = 0; j < this.size; j++) {
                Coordinates c = new Coordinates(j, i);
                if (map.containsKey(c)) {
                    if (map.get(c).getColor() == 'B') {
                        System.out.print("\u001B[34m███\u001B[0m");
                    } else {
                        System.out.print("\u001B[33m███\u001B[0m");
                    }
                } else {
                    System.out.print("   ");
                }
                System.out.print("│");
            }
            System.out.println();

            if (i < this.size - 1) {
                System.out.print(" ├─");
                for (int j = 0; j < this.size - 1; j++) {
                    System.out.print("──┼─");
                }
                System.out.println("──┤");
            }
        }

        System.out.print(" └─");
        for (int i = 0; i < this.size - 1; i++) {
            System.out.print("──┴─");
        }
        System.out.println("──┘");
    }

    /**
     * Cette méthode permet trouver les coordonnées des cellules vides du plateau.
     * @return Set<Coordinates> qui contient les coordonnées des cellules vides du plateau.
     */
    public List<Coordinates> getValidEmptyCoordinates () {
        
        // On récupère les coordonnées des cellules non vides
        Set<Coordinates> nonEmptyCells = map.keySet();

        // On initialise un Set qui contiendra les coordonnées des cellules vides
        List<Coordinates> emptyCells = new ArrayList<>();

        // On parcourt le plateau pour trouver les cellules vides
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Coordinates coords = new Coordinates(i, j);

                // Si la cellule n'est pas dans le Set des cellules non vides, on l'ajoute au Set des cellules vides
                if (!nonEmptyCells.contains(coords) && (hasNeighbours(coords) || i == 0 || i == size - 1 || j == 0 || j == size - 1)) {
                    emptyCells.add(coords);
                }
            }
        }

        return emptyCells;
    }

    /**
     * Cette méthode permet de récupérer les coordonnées des jetons du joueur.
     * @return List<Coordinates> qui contient les coordonnées des jetons du joueur.
     */
    public List<Coordinates> getColorTokenCoordinates (char color) {

        // On initialise une liste qui contiendra les coordonnées des jetons du joueur
        List<Coordinates> ownTokens = new ArrayList<>();

        // On parcourt l'ensemble des jetons du posés sur le plateau
        for (Coordinates coords : map.keySet()) {

            // Si la couleur du jeton est celle du joueur, on l'ajoute à la liste des jetons du joueur
            if (getToken(coords).getColor()  == color) {
                ownTokens.add(coords);
            }
        }

        return ownTokens;
    }

    public boolean hasColorInDirection(Coordinates coords, int[] direction, char color) {

        // On vérifie si la cellule est sur un bord du plateau
        if (isOnBorder(coords)) {
            return false;
        }
        // Si les prochaines coordonnées sont dans le plateau et que la couleur est celle du joueur, on retourne vrai
        Coordinates nextCoords = new Coordinates(coords.getX() + direction[0], coords.getY() + direction[1]);
        if (map.keySet().contains(nextCoords) && map.get(nextCoords).getColor() == color) {
            return true;
        }
        // Sinon on continue de vérifier dans la même direction
        return hasColorInDirection(nextCoords, direction, color);
    }

    public boolean isAllignedWithColor (Coordinates coords, char color) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        // On vérifie s'il y a un jeton de la couleur donnée dans chaque direction
        for (int[] direction : directions) {
            if (hasColorInDirection(coords, direction, color)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasValidPush(char color) {
        EmptyCoordIterator emptyCoordIterator = new EmptyCoordIterator(this);

        // Tant qu'il reste des cellules vides à explorer
        while (emptyCoordIterator.hasNext()) {

            // On vérifie si la cellule vide est alignée avec un jeton du joueur
            Coordinates emptyCoordinates = emptyCoordIterator.next();
            if (isAllignedWithColor(emptyCoordinates, color)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnBorder(Coordinates coordinates) {
        return coordinates.getX() == 0 || coordinates.getX() == size - 1 || coordinates.getY() == 0 || coordinates.getY() == size - 1;
    }
}
 



