package agentsPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import gamePackage.Action;
import gamePackage.Coordinates;
import gamePackage.Grid;

public abstract class Agent {

    private char color;
    private int[] scores;


    public abstract Action evaluateAction(Grid grid);
    public abstract void executeAction(Action action);

    public char getColor() {
        return this.color;
    }


    public Agent(char myColor) {
        this.color = myColor;
    }

    public Agent(char myColor, int[] scores) {
        this.color = myColor;
        this.scores = scores;
    }

    public HashMap<String, List<List<Coordinates>>> updateAgentScore(Grid grid) {
        // Déterminer la couleur de l'adversaire
        char opponentColor = getColor() == 'B' ? 'Y' : 'B';

        // Récupérer les alignements pour l'agent et l'adversaire
        List<List<Coordinates>> myAlignments = grid.getAlignments(getColor(), 5);
        List<List<Coordinates>> opponentAlignments = grid.getAlignments(opponentColor, 5);
        grid.clearAlignments(myAlignments);
        grid.clearAlignments(opponentAlignments);


        // Compter le nombre d'alignements
        int nbAlignements = myAlignments.size();
        int nbOpponentAlignements = opponentAlignments.size();

        // Déterminer le nombre minimum d'alignements
        int minAlignements = Math.min(nbAlignements, nbOpponentAlignements);

        // Ajuster les comptes en supprimant les alignements communs minimums
        nbAlignements -= minAlignements;
        nbOpponentAlignements -= minAlignements;

        // Mettre à jour les scores en fonction des alignements restants
        HashMap<String, List<List<Coordinates>>> alignmentsMap = new HashMap<>();

        if (getColor() == 'B') {
            scores[0] += nbAlignements;
            scores[1] += nbOpponentAlignements;

            alignmentsMap.put("B", myAlignments);
            alignmentsMap.put("Y", opponentAlignments);

        } else {
            scores[0] += nbOpponentAlignements;
            scores[1] += nbAlignements;

            alignmentsMap.put("Y", myAlignments);
            alignmentsMap.put("B", opponentAlignments);
        }

        // Retourner le map contenant les alignements
        return alignmentsMap;
    }
}  
