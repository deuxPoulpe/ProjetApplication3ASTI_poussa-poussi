package myPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmartAgent extends Agent{

    private int smartness;
    private final int[] weights = {1, 2, 4, 8};
    
    public SmartAgent(char myColor, int smartness) {
        super(myColor);
        this.smartness = smartness;
    }

    public void placeToken(Grid grid) {
        // TODO Auto-generated method stub
        
        
    }
    
    public void pushToken(Grid grid) {
        // TODO Auto-generated method stub
        
    }

    public void removeTwoTokens(Grid grid, List<Coordinates> alignment) {
        // TODO Auto-generated method stub
        
    }

    public int evaluateGameConfiguration(Grid grid) {

        // On récupère les alignements de chaque joueur
        int[][] alignmentCounts = getAlignmentCounts(grid);

        System.out.println("Self alignments: " + alignmentCounts[0][0] + " " + alignmentCounts[0][1] + " " + alignmentCounts[0][2] + " " + alignmentCounts[0][3]);
        System.out.println("Opponent alignments: " + alignmentCounts[1][0] + " " + alignmentCounts[1][1] + " " + alignmentCounts[1][2] + " " + alignmentCounts[1][3]);

        // On calcule et retourne le score de la configuration de jeu
        return calculateScore(alignmentCounts[0], alignmentCounts[1]);
    }

    public int[][] getAlignmentCounts(Grid grid) {
        // On initialise les compteurs d'alignements de  chaque joueur
        int[] selfAlignmentsCount = new int[4];
        int[] opponentAlignmentsCount = new int[4];

        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        Set<Coordinates> tokenSet = grid.getHashMap().keySet();
           
        // On parcourt les jetons du plateau
        for (Coordinates coords : tokenSet) {

            // On récupère les alignements du jeton
            for (int[] direction : directions) {

                // Si le jeton n'est pas déjà dans l'alignement
                if (!grid.getToken(coords).getAlignments().contains(direction)) {

                    Set<Coordinates> visited = new HashSet<>();
                    List<Coordinates> alignment = grid.getAlignment(coords, direction, visited);

                    // On incrémente les compteurs d'alignements
                    if (alignment.size() > 1) {
                        if (grid.getToken(coords).getColor() == super.getColor()) {
                            selfAlignmentsCount[alignment.size() - 2]++;
                        } else {
                            opponentAlignmentsCount[alignment.size() - 2]++;
                        }

                        // On ajoute les alignements à la liste des alignements des jetons
                        for (Coordinates alignmentCoords : alignment) {
                            grid.getToken(alignmentCoords).addToAlignments(direction);
                        }
                    }
                }

            }
            
        }
        return new int[][] {selfAlignmentsCount, opponentAlignmentsCount};
    }

    public int calculateScore(int[] selfAlignmentsCount, int[] opponentAlignmentsCount) {
        int score = 0;
        for (int i = 0; i < 4; i++) {
            score += (selfAlignmentsCount[i] - opponentAlignmentsCount[i]) * weights[i];
        }
        return score;
    }

    public int[] minMaxAlphaBeta(Grid grid, int depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int i = 0; i < depth; i++) {

        }
        GridTree root = new GridTree(null, grid);

    
    }

    public HashMap<Coordinates, List<PushAction>>  getAllValidActions(Grid grid) {

        HashMap<Coordinates, List<PushAction>> actions = new HashMap<>();
        
        // Pour chaque cellule vide
        List<Coordinates> emptyCells = getValidEmptyCells(grid);
        for (Coordinates emptyCellCoords : emptyCells) {

            // On inialise un clone du plateau avec le jeton placé
            Grid placeGrid = grid.clone();
            placeGrid.placeToken(super.getColor(), emptyCellCoords);
            
            
            // Pour chaque jeton du joueur sur le plateau cloné
            List<Coordinates> ownTokens = super.getOwnTokensCoords(placeGrid);
            for (Coordinates ownTokenCoords : ownTokens) {

                // On itère sur les directions de poussée valides
                List<int[]> validDirections = super.getValidPushDirections(placeGrid, ownTokenCoords);
                for (int[] direction : validDirections) {

                    // On ajoute l'action à la liste des actions possibles
                    PushAction pushAction = new PushAction(ownTokenCoords, direction);
                    actions.get(emptyCellCoords).add(pushAction);
                }
            }
        }
        return actions;
    }
}
