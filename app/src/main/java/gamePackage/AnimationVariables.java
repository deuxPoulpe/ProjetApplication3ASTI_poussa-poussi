package gamePackage;

import java.util.HashMap;


/**
 * This class is used to store the variables needed to animate a move.
 */
public class AnimationVariables {
    
    private int nbCasesToPush;
    private int[] directions;
    private HashMap<Coordinates,Token> tokensToAnimate;

    private static AnimationVariables instance = null;
    
    public AnimationVariables(int nbCasesToPush, int[] directions, HashMap<Coordinates,Token> tokensToAnimate) {
        this.nbCasesToPush = nbCasesToPush;
        this.directions = directions;
        this.tokensToAnimate = tokensToAnimate;
    }

    public void setAnimationVariables(int nbCasesToPush, int[] directions, HashMap<Coordinates,Token> tokensToAnimate) {
        this.nbCasesToPush = nbCasesToPush;
        this.directions = directions;
        this.tokensToAnimate = tokensToAnimate;
    }

    
    public AnimationVariables(){
        this.nbCasesToPush = 0;
        this.directions = null;
        this.tokensToAnimate = null;
    }
    
    public int getNbCasesToPush() {
        return this.nbCasesToPush;
    }
    
    public int[] getDirections() {
        return this.directions;
    }
    
    public HashMap<Coordinates,Token> getTokensToAnimate() {
        return this.tokensToAnimate;
    }
}
