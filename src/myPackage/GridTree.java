package myPackage;

import java.util.List;

public class GridTree {

    private final GridTree parent;
    private List<GridTree> children = null;
    private Grid grid;
    private Coordinates placeCoordinates;
    private PushAction pushAction;
    private int heuristicValue;
    private SmartAgent agent;

    // Constructeur pour la racine de l'arbre
    public GridTree(SmartAgent agent, Grid grid) {
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
                    GridTree child = new GridTree(this, pushGrid, emptyCellCoords, pushAction);
                    addChild(child);

                    System.out.println("Child added: " + child.getPlaceCoordinates().toString() + " " + child.getPushAction().toString());
                }
            }
        }
    }

    public void calculateHeuristicValue() {

        // On récupère les alignements de chaque joueur
        int[][] alignmentCounts = grid.getAlignmentCounts(agent.getColor());

        System.out.println("Self alignments: " + alignmentCounts[0][0] + " " + alignmentCounts[0][1] + " " + alignmentCounts[0][2] + " " + alignmentCounts[0][3]);
        System.out.println("Opponent alignments: " + alignmentCounts[1][0] + " " + alignmentCounts[1][1] + " " + alignmentCounts[1][2] + " " + alignmentCounts[1][3]);

        // On calcule et retourne le score de la configuration de jeu
        heuristicValue = calculateScore(alignmentCounts[0], alignmentCounts[1]);
    }

    public int calculateScore(int[] selfAlignmentsCount, int[] opponentAlignmentsCount) {
        int score = 0;
        for (int i = 0; i < 4; i++) {
            score += (selfAlignmentsCount[i] - opponentAlignmentsCount[i]) * agent.getWeights()[i];
        }
        return score;
    }
}
