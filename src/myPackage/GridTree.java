package myPackage;

import java.util.ArrayList;
import java.util.List;

public class GridTree {

    private final GridTree parent;
    private List<GridTree> children = new ArrayList<>();
    private Grid grid;
    private Coordinates placeCoordinates;
    private PushAction pushAction;
    private Coordinates[] removCoordinates = new Coordinates[2];
    private int heuristicValue;
    private MinMaxAgent agent;

    // Constructeur pour la racine de l'arbre
    public GridTree(MinMaxAgent agent, Grid grid) {
        this.agent = agent;
        this.grid = grid;
        this.parent = null;
    }

    // Constructeur pour les noeuds de l'arbre
    public GridTree(GridTree parent, Grid grid, Coordinates placeCoordinates, PushAction pushAction) {
        this.agent = parent.agent;
        this.parent = parent;
        this.grid = grid;
        this.placeCoordinates = placeCoordinates;
        this.pushAction = pushAction;
    }

    // Constructeur pour les noeuds de l'arbre avec suppression de jetons
    public GridTree(GridTree parent, Grid grid, Coordinates[] removCoordinates) {
        this.agent = parent.agent;
        this.parent = parent;
        this.grid = grid;
        this.removCoordinates = removCoordinates;
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

    public Coordinates[] getRemovCoordinates() {
        return removCoordinates;
    }

    public GridTree getParent() {
        return parent;
    }

    public List<GridTree> getChildren() {
        return children;
    }

    public void addChild(GridTree child) {
        children.add(child);
    }

    public boolean isLeaf() {
        return children == null;
    }

    public void generateChildNodes() {

        GridTree node = null;

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
                        node = new GridTree(this, removeGrid, coordsToRemove);
                        addChild(node);
                    }
                }
            }
        }

        if (node == null) {
            node = this;
        }
        
        // Pour chaque cellule vide
        List<Coordinates> emptyCells = agent.getValidEmptyCells(grid);
        for (Coordinates emptyCellCoords : emptyCells) {

            // On inialise un clone du plateau avec le jeton placé
            Grid placeGrid = grid.clone();
            placeGrid.placeToken(agent.getColor(), emptyCellCoords);
            
            
            // Pour chaque jeton du joueur sur le plateau cloné
            List<Coordinates> ownTokens = agent.getOwnTokensCoords(placeGrid);
            for (Coordinates ownTokenCoords : ownTokens) {

                // On itère sur les directions de poussée valides
                List<int[]> validDirections = agent.getValidPushDirections(placeGrid, ownTokenCoords);
                for (int[] direction : validDirections) {

                    // On effectue la poussée et on ajoute la grille en tant que fils du noeud
                    Grid pushGrid = placeGrid.clone();
                    pushGrid.pushToken(agent.getColor(), ownTokenCoords, direction);
                    PushAction pushAction = new PushAction(ownTokenCoords, direction);

                    // Sinon, on ajoute la grille en tant que fils du noeud
                    GridTree child = new GridTree(node, pushGrid, emptyCellCoords, pushAction);
                    addChild(child);
                }
            }
        }
    }

    public void calculateHeuristicValue() {
        int[] alignmentCounts = new int[4];
        int[] opponentAlignmentCounts = new int[4];

        // On récupère les alignements de chaque joueur
        for (int i = 2; i < 6; i++) {
            // joueur courant
            alignmentCounts[i] = grid.getAlignments(agent.getColor(), i).size();

            // adversaire
            if (agent.getColor() == 'B') {
                opponentAlignmentCounts[i] = grid.getAlignments('Y', i).size();
            } else {
                opponentAlignmentCounts[i] = grid.getAlignments('B', i).size();
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
}
