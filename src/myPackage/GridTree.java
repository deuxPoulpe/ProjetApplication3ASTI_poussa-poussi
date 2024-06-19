package myPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class GridTree {

    private final GridTree parent;
    private List<GridTree> children = new ArrayList<>();
    private Grid grid;
    private Coordinates placeCoordinates;
    private PushAction pushAction;
    private List<List<Coordinates[]>> removCoordinates = new ArrayList<>(); // Première liste pour les coordonnées des jetons à retirer en début de tour, deuxième pour les jetons à retirer en fin de tour
    private int heuristicValue;
    private MinMaxAgent agent;

    // Constructeur pour la racine de l'arbre
    public GridTree(MinMaxAgent agent, Grid grid) {
        this.agent = agent;
        this.grid = grid;
        this.parent = null;
        this.removCoordinates.add(new ArrayList<>());
        this.removCoordinates.add(new ArrayList<>());
    }

    // Constructeur pour les noeuds de l'arbre
    public GridTree(GridTree myParent, Grid grid, Coordinates placeCoordinates, PushAction pushAction) {
        this.agent = myParent.agent;
        this.removCoordinates = myParent.removCoordinates;
        this.parent = myParent;
        this.grid = grid;
        this.placeCoordinates = placeCoordinates;
        this.pushAction = pushAction;
    }

    // Constructeur pour les noeuds de l'arbre avec suppression de jetons
    public GridTree(GridTree parent, Grid grid, List<List<Coordinates[]>> removCoordinates) {
        this.agent = parent.agent;
        this.parent = parent;
        this.grid = grid;
        this.removCoordinates = removCoordinates;
    }

    public String toString() {
        String strRemovCoordinates1 = "Tokens to remove first: " + removCoordinates.get(0) + "\n";
        String strPlaceCoordinates = "Place coordinates: " + placeCoordinates + "\n";
        String strPushAction = "Push action: " + pushAction + "\n";
        String strRemovCoordinates2 = "Tokens to remove second: " + removCoordinates.get(1) + "\n";

        return strRemovCoordinates1 + strPlaceCoordinates + strPushAction + strRemovCoordinates2;
    }


    public int getHeuristicValue() {
        return heuristicValue;
    }

    public Grid getGrid() {
        return grid;
    }

    public Coordinates getPlaceCoordinates() {
        return placeCoordinates;
    }

    public PushAction getPushAction() {
        return pushAction;
    }

    public List<List<Coordinates[]>> getRemovCoordinates() {
        return removCoordinates;
    }

    public GridTree getParent() {
        return parent;
    }

    public List<GridTree> getChildren() {
        return children;
    }

    public void setRemovCoordinates(List<List<Coordinates[]>> removCoordinates) {
        this.removCoordinates = removCoordinates;
    }

    public void addChild(GridTree child) {
        children.add(child);
    }

    public boolean isLeaf() {
        return children == null;
    }

    public void calculateHeuristicValue() {
        int[] alignmentCounts = new int[4];
        int[] opponentAlignmentCounts = new int[4];

        // On récupère les alignements de chaque joueur
        for (int i = 2; i < 6; i++) {
            // joueur courant
            alignmentCounts[i - 2] = grid.getAlignments(agent.getColor(), i).size();

            // adversaire
            if (agent.getColor() == 'B') {
                opponentAlignmentCounts[i - 2] = grid.getAlignments('Y', i).size();
            } else {
                opponentAlignmentCounts[i - 2] = grid.getAlignments('B', i).size();
            }
        }

        // On calcule et retourne le score de la configuration de jeu
        heuristicValue = calculateScore(alignmentCounts, alignmentCounts);
    }

    public int calculateScore(int[] alignmentCount, int[] opponentAlignmentCount) {
        int score = 0;
        for (int i = 0; i < 4; i++) {
            score += (alignmentCount[i] - opponentAlignmentCount[i]) * agent.getWeights()[i];
        }
        return score;
    }

    private HashMap<Coordinates[], Grid> getRemovGrids(Grid inputGrid) {

        HashMap<Coordinates[], Grid> gridMap = new HashMap<>();

        // Pour chaque alignement de 5 jetons du joueur
        for (List<Coordinates> alignement : grid.getAlignments(agent.getColor(), 5)) {

            // On incrémente le score du joueur
            if (Settings.getInstance().getDisplayInTerminal())
                System.out.println(agent.getColor() + " made an alignment of 5 !");
            agent.incrementScore(1);
            
            for (Coordinates removCoords1 : alignement) {
                for (Coordinates removCoords2 : alignement) {
                    if (removCoords1 != removCoords2) {
                        
                        // On clone le plateau et on retire les deux jetons
                        Grid removeGrid = grid.clone();
                        removeGrid.removeToken(removCoords1);
                        removeGrid.removeToken(removCoords2);
                        
                        // On ajoute la grille en tant que fils du noeud
                        Coordinates[] coordsToRemove = {removCoords1, removCoords2};
                        
                        gridMap.put(coordsToRemove, removeGrid);
                    }
                }
            }
        }
        return gridMap;
    }

    private List<GridTree> getPlaceChildren() {
        List<GridTree> childList = new ArrayList<>();

        HashMap<Coordinates[], Grid> removGrids = getRemovGrids(grid);
        if (removGrids.isEmpty()) {
            // Pour chaque cellule vide
            List<Coordinates> emptyCells = agent.getValidEmptyCells(grid);
            for (Coordinates emptyCellCoords : emptyCells) {
    
                // On inialise un clone du plateau avec le jeton placé
                Grid placeGrid = grid.clone();
                placeGrid.placeToken(agent.getColor(), emptyCellCoords);
                GridTree child = new GridTree(this, placeGrid, emptyCellCoords, null);
                childList.add(child);
            }
        } else {
            // Pour chaque grille obtenue après avoir retiré 2 jetons de l'alignement
            for (Coordinates[] childRemovCoords : removGrids.keySet()) {

                // Pour chaque cellule vide
                List<Coordinates> emptyCells = agent.getValidEmptyCells(removGrids.get(childRemovCoords));
                for (Coordinates emptyCellCoords : emptyCells) {

                    // On inialise un clone du plateau avec le jeton placé
                    Grid placeGrid = removGrids.get(childRemovCoords).clone();
                    placeGrid.placeToken(agent.getColor(), emptyCellCoords);

                    List<List<Coordinates[]>> childRemovCoordinates = removCoordinates;
                    childRemovCoordinates.get(0).add(childRemovCoords);
                    GridTree child = new GridTree(this, placeGrid, childRemovCoordinates);
                    childList.add(child);
                }
            }
        }
        return childList;
    }

    private List<GridTree> getPushChildren() {
            
            List<GridTree> childList = new ArrayList<>();
    
            // Pour chaque jeton du joueur sur le plateau
            List<Coordinates> ownTokens = agent.getOwnTokensCoords(grid);
            for (Coordinates ownTokenCoords : ownTokens) {
    
                // Pour chaque direction de poussée valide
                List<int[]> validDirections = agent.getValidPushDirections(grid, ownTokenCoords);
                for (int[] direction : validDirections) {
    
                    // On effectue la poussée sur une copie du plateau
                    Grid pushGrid = grid.clone();
                    pushGrid.pushToken(agent.getColor(), ownTokenCoords, direction);
                    PushAction pushAction = new PushAction(ownTokenCoords, direction);

                    // Si aucun alignement de 5 de notre couleur n'a été formé, on ajoute la grille en tant que fils du noeud
                    HashMap<Coordinates[], Grid> removGrids = getRemovGrids(pushGrid);
                    if (removGrids.isEmpty()) {
                        GridTree child = new GridTree(this, pushGrid, placeCoordinates , pushAction);
                        childList.add(child);
                    } else {
                        // Sinon, on ajoute les grilles obtenues après avoir retiré 2 jetons de l'alignement
                        for (Coordinates[] childRemovCoords : removGrids.keySet()) {
                            List<List<Coordinates[]>> childRemovCoordinates = removCoordinates;
                            childRemovCoordinates.get(1).add(childRemovCoords);
                            GridTree child = new GridTree(this, pushGrid, childRemovCoordinates);
                            childList.add(child);
                        }
                    }
                }
            }
            return childList;
    }

    public void generateChildNodes() {
        for (GridTree placeChild : getPlaceChildren()) {
            for (GridTree pushChild : placeChild.getPushChildren()) {
                addChild(pushChild);
            }
        }
    }

}
