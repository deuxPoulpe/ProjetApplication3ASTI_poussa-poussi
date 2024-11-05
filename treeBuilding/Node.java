package treeBuilding;

import agents.MinMaxAI;
import game.Board;

public class Node {

    // Caractéristiques du noeud
    private MinMaxAI agent;
    private int heuristicValue = 0;
    private int depth = 0;
    private Board board;


    // Constructeur pour la racine de l'arbre
    public Node(MinMaxAI agent, Board board) {
        this.agent = agent;
        this.depth = 0;
        this.board = board;
    }

    // Constructeur pour les noeuds de l'arbre
    public Node(Node parent, Board board) {

        this.agent = parent.agent;
        this.board = board;
    }

    public int getDepth() {
        return depth;
    }

    public MinMaxAI getAgent() {
        return agent;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public Board getBoard() {
        return board;
    }

    public void incrementDepth() {
        depth++;
    }

}