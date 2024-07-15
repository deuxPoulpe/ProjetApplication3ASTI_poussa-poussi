package com.example.poussapoussi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poussapoussi.databinding.FragmentGridBinding;

import agentsPackage.MinMaxAgent;
import agentsPackage.PlayerAgent;
import gamePackage.*;
import UtilsPackage.*;
import treeFormationPackage.ActionTree;


//test
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 * This fragment is used to display the game grid and handle the game logic.
 * It allows the player to place and push tokens, and the AI to play its turn.
 */
public class GridFragment extends Fragment {
    private FragmentGridBinding binding;

    private final boolean allowToPrintMessages = false;

    private Grid grid;
    private Game game;
    private char choice;
    private boolean pushTurn = false;
    private boolean placeTurn = true;

    private HashMap<List<Coordinates>, Integer> removeTurnPlayer1 = new HashMap<>();
    private HashMap<List<Coordinates>, Integer> removeTurnPlayer2 = new HashMap<>();

    private char firstRemovePlayer;

    private boolean finishGame = false;


    private boolean aiTurn = false;

    private Coordinates coordinatesToPush = null;
    private HomePageFragment homePageFragment;

    private int orangeAiDifficulty;
    private int blueAiDifficulty;

    private AnimationVariables animationVariables;

    private boolean resume = false;
    private boolean terminal = false;

    public GridFragment() {
        // Required empty public constructor

    }

    public GridFragment(char choice, HomePageFragment homePageFragment, int orangeAiDifficulty, int blueAiDifficulty) {
        // Required empty public constructor
        this.choice = choice;
        this.homePageFragment = homePageFragment;
        this.orangeAiDifficulty = orangeAiDifficulty;
        this.blueAiDifficulty = blueAiDifficulty;

    }

    public Grid getGrid() {
        return grid;
    }

    public void setResume(boolean resume) {
        this.resume = resume;
    }


    public boolean isTerminal() {
        return terminal;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GridFragment.
     */
    public static GridFragment newInstance(char choice) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putChar("choice", choice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.choice = getArguments().getChar("choice");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGridBinding.inflate(inflater, container, false);
        Settings.getInstance(false, false, true);

        initSkipButton();
        initGame();

        binding.mainMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToHomeFragment();
            }
        });

        binding.restartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToHomeFragment();
            }
        });

        return binding.getRoot();
    }

    /**
     * Navigate to the home fragment
     */
    private void navigateToHomeFragment() {
        this.finishGame = true;
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homePageFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Initialize the game
     */
    private void initGame(){
        if(!resume) {
            animationVariables = new AnimationVariables();
            this.grid = new Grid(animationVariables);

            //############################### TEST ########################################//
////
//            grid.placeToken('B', new Coordinates(0, 0));
//            grid.placeToken('B', new Coordinates(1, 0));
//            grid.placeToken('B', new Coordinates(2, 0));
//            grid.placeToken('B', new Coordinates(3, 0));
//            grid.placeToken('Y', new Coordinates(6, 0));
//
//
//            grid.placeToken('B', new Coordinates(0, 1));
//            grid.placeToken('Y', new Coordinates(1, 1));
//            grid.placeToken('B', new Coordinates(2, 1));
//            grid.placeToken('Y', new Coordinates(3, 1));
//            grid.placeToken('B', new Coordinates(4, 1));
//            grid.placeToken('Y', new Coordinates(5, 1));
//            grid.placeToken('B', new Coordinates(6, 1));
//            grid.placeToken('Y', new Coordinates(7, 1));
//
//            grid.placeToken('B', new Coordinates(0, 2));
//            grid.placeToken('Y', new Coordinates(1, 2));
//            grid.placeToken('B', new Coordinates(2, 2));
//            grid.placeToken('Y', new Coordinates(3, 2));
//            grid.placeToken('B', new Coordinates(4, 2));
//            grid.placeToken('Y', new Coordinates(5, 2));
//            grid.placeToken('B', new Coordinates(6, 2));
//            grid.placeToken('Y', new Coordinates(7, 2));
//
//            grid.placeToken('B', new Coordinates(0, 3));
//            grid.placeToken('Y', new Coordinates(1, 3));
//            grid.placeToken('B', new Coordinates(2, 3));
//            grid.placeToken('Y', new Coordinates(3, 3));
//            grid.placeToken('B', new Coordinates(4, 3));
//            grid.placeToken('Y', new Coordinates(5, 3));
//            grid.placeToken('B', new Coordinates(6, 3));
//            grid.placeToken('Y', new Coordinates(7, 3));
//
//            grid.placeToken('Y', new Coordinates(0, 4));
//            grid.placeToken('B', new Coordinates(1, 4));
//            grid.placeToken('Y', new Coordinates(2, 4));
//            grid.placeToken('B', new Coordinates(3, 4));
//            grid.placeToken('Y', new Coordinates(4, 4));
//            grid.placeToken('B', new Coordinates(5, 4));
//            grid.placeToken('Y', new Coordinates(6, 4));
//            grid.placeToken('B', new Coordinates(7, 4));
//
//            grid.placeToken('Y', new Coordinates(0, 5));
//            grid.placeToken('B', new Coordinates(1, 5));
//            grid.placeToken('Y', new Coordinates(2, 5));
//            grid.placeToken('B', new Coordinates(3, 5));
//            grid.placeToken('Y', new Coordinates(4, 5));
//            grid.placeToken('B', new Coordinates(5, 5));
//            grid.placeToken('Y', new Coordinates(6, 5));
//            grid.placeToken('B', new Coordinates(7, 5));
//
//            grid.placeToken('B', new Coordinates(0, 6));
//            grid.placeToken('Y', new Coordinates(1, 6));
//            grid.placeToken('B', new Coordinates(2, 6));
//            grid.placeToken('Y', new Coordinates(3, 6));
//            grid.placeToken('B', new Coordinates(4, 6));
//            grid.placeToken('Y', new Coordinates(5, 6));
//            grid.placeToken('B', new Coordinates(6, 6));
//            grid.placeToken('Y', new Coordinates(7, 6));
//
//            grid.placeToken('B', new Coordinates(0, 7));
//            grid.placeToken('Y', new Coordinates(1, 7));
//            grid.placeToken('B', new Coordinates(2, 7));
//            grid.placeToken('Y', new Coordinates(3, 7));
//            grid.placeToken('B', new Coordinates(4, 7));
//            grid.placeToken('Y', new Coordinates(5, 7));
//            grid.placeToken('B', new Coordinates(6, 7));
//            grid.placeToken('Y', new Coordinates(7, 7));


            //############################### TEST ########################################//

            this.game = new Game(this.grid, this.blueAiDifficulty, this.orangeAiDifficulty);
            this.game.start(this.choice);
        }
        this.finishGame = false;

        if (choice == '3' ){
            this.aiTurn = true;
            DelayedTaskUtil.executeWithDelay(500, this::playAITurn);
        }

        changeColorToCurrentPlayer();
        updateDisplayScore();
        displayTokenGrid();
        displayBorderGrid();
        displayTurn();


    }


    /**
     * Initialize the skip button
     */
    private void initSkipButton(){
        binding.blueSkip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                skipPushTurn();
            }
        });
        binding.orangeSkip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                skipPushTurn();
            }
        });
    }

    /**
     * Display the grid of borders in the GridLayout
     */
    private void displayBorderGrid() {
        GridLayout borderGridLayout = binding.borderGridLayout;
        borderGridLayout.removeAllViews();

        int size = 8;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ImageView cell = new ImageView(getContext());
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = param.height = (getResources().getDisplayMetrics().widthPixels) / size;
                param.columnSpec = GridLayout.spec(i, 1f);
                param.rowSpec = GridLayout.spec(j, 1f);
                cell.setLayoutParams(param);

                cell.setBackgroundResource(R.drawable.cell_border); // Add a drawable for cell border


                final int finalI = i;
                final int finalJ = j;
                cell.setClickable(true); // Make the cell clickable
                cell.setFocusable(true); // Make the cell focusable
                cell.setOnClickListener(v -> handleCellClickPlace(finalI, finalJ));
                borderGridLayout.addView(cell);
            }
        }
    }

    /**
     * Handle cell click event for placing a token
     * @param i : x coordinate of the cell
     * @param j : y coordinate of the cell
     */
    private void handleCellClickPlace(int i, int j) {
        Coordinates coordinates = new Coordinates(i, j);
        if(this.finishGame){
            printTest("The game is finished");
            return;
        }
        if(this.aiTurn){
            printTest("It's not your turn");
            return;
        }

        if(!this.removeTurnPlayer1.isEmpty() || !this.removeTurnPlayer2.isEmpty()){
            printTest("You can't place now need to remove token before");
            return;
        }
        if (this.grid.getToken(coordinates) == null && placeTurn && !aiTurn) {
            try {
                this.grid.placeToken(game.getColorOfCurrentPlayer(), coordinates);
                handleTurn();

            } catch (IllegalArgumentException e) {
                printTest(e.getMessage());
            }
        }
        else {
            if (!placeTurn){
                printTest("You can't place now");
            }
            else if (aiTurn) {
                printTest("It's not your turn");
            }
            else {
                printTest("This cell is already occupied");
            }
        }
    }

    /**
     * Change background color to match the current player's color
     */
    private void changeColorToCurrentPlayer(){
        if (game.getColorOfCurrentPlayer() == 'B') {
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#385ea0"));
        }
        else {
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#ce9744"));
        }

        if(grid.isFull()){
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#777777"));
        }
    }

    /**
     * Handle the current turn
     * this.placeTurn : the player can place a token
     * this.pushTurn : the player can push a token
     *
     */
    private void handleTurn() {
        if (this.finishGame) {
            endTurn();
            return;
        }

//        if (!this.removeTurnPlayer1.isEmpty() || !this.removeTurnPlayer2.isEmpty()) {
//            displayTokenGrid();
//            displayBorderGrid();
//            return;
//        }
// =================== THIS WILL MAY BE PROBLEMATIC ===================


        if (this.placeTurn) {
            this.pushTurn = true;
            this.placeTurn = false;

            displayTurn();
        } else if (this.pushTurn) {
            endTurn();
        }


        displayTokenGrid();
        displayBorderGrid();

        if (this.aiTurn && (this.removeTurnPlayer1.isEmpty() && this.removeTurnPlayer2.isEmpty())) {
            DelayedTaskUtil.executeWithDelay(500, this::playAITurn);
        }
    }


    /**
     * End the current turn
     * Check for a win or alignments of five tokens
     * Switch player
     * Change background color to match the current player's color
     */
    private void endTurn(){
        this.pushTurn = false;
        this.placeTurn = true;

        checkWin();

        if (this.choice == '2') {
            this.aiTurn = !this.aiTurn;
        }


        updateDisplayScore();
        if (removeTurnPlayer1.isEmpty() && removeTurnPlayer2.isEmpty()) {
            game.switchPlayer();
            changeColorToCurrentPlayer();

        }
        displayTurn();

    }

    /**
     * Check for a win or alignments of five tokens
     */
    private void checkWin() {


        if(!this.removeTurnPlayer1.isEmpty() || !this.removeTurnPlayer2.isEmpty()){
            printTest("the score has already been updated");
            return;
        }

        HashMap<String, List<List<Coordinates>>> alignments = game.getCurrentPlayer().updateAgentScore(grid);
        List<List<Coordinates>> blueAlignments = alignments.get("B");
        List<List<Coordinates>> orangeAlignments = alignments.get("Y");

        if (grid.isFull() && blueAlignments.isEmpty() && orangeAlignments.isEmpty()){
            displayWinner("Draw");
            return;
        }

        firstRemovePlayer = game.getColorOfCurrentPlayer();

        //set remove two token for each player if they have an alignment of 5
        if (blueAlignments.size() > 0 && choice != '3') {
            for (List<Coordinates> alignment : blueAlignments) {
                this.removeTurnPlayer1.put(alignment, 2);
            }
        }
        if (orangeAlignments.size() > 0 && choice == '1') {
            for (List<Coordinates> alignment : orangeAlignments) {
                this.removeTurnPlayer2.put(alignment, 2);
            }
        }


        boolean shouldSwitchPlayer = (firstRemovePlayer == 'B')
                ? this.removeTurnPlayer1.isEmpty() && !this.removeTurnPlayer2.isEmpty()
                : !this.removeTurnPlayer1.isEmpty() && this.removeTurnPlayer2.isEmpty();

        if (shouldSwitchPlayer) {
            game.switchPlayer();
            changeColorToCurrentPlayer();
        }


        if (game.getScores()[0] >= 2) {
            displayWinner("Blue");
            this.finishGame = true;
            this.terminal = true;
        } else if (game.getScores()[1] >= 2) {
            displayWinner("Orange");
            this.finishGame = true;
            this.terminal = true;
        }

    }

    /**
     * Update the score display
     */
    @SuppressLint("SetTextI18n")
    private void updateDisplayScore() {
        TextView scoreView = binding.scoreBoard;
        int[] scores = game.getScores();
        String scoreText = scores[0] + " • " + scores[1];

        // Créez un SpannableString avec le texte du score
        SpannableString spannableString = new SpannableString(scoreText);

        // Appliquez la couleur bleue au premier score
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#385ea0")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Appliquez la couleur orange au deuxième score
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ce9744")), 1 + 3, scoreText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Définissez le texte coloré sur le TextView
        scoreView.setText(spannableString);
    }

    /**
     * Display the winner and navigate to the home fragment
     * @param winner : the winner's color
     */
    @SuppressLint("SetTextI18n")
    public void displayWinner(String winner) {
        changeColorToCurrentPlayer();

        // Mettez à jour le texte du TextView
        TextView winnerTextView = binding.winnerTextView;
        View restartButton = binding.restartButton;
        winnerTextView.setText("Winner: " + winner);
        if (winner.equals("Draw")) {
            winnerTextView.setText("Draw");
            winnerTextView.setTextColor(Color.parseColor("#777777"));
        }

        //change background color of the full screen with opacity
        if (winner.equals("Blue")) {
            binding.winnerBackground.setBackgroundColor(Color.parseColor("#BB385ea0"));
        } else if (winner.equals("Orange")){
            binding.winnerBackground.setBackgroundColor(Color.parseColor("#BBce9744"));
        }
        else {
            binding.winnerBackground.setBackgroundColor(Color.parseColor("#DD303030"));
        }

        // Rendez le TextView visible
        winnerTextView.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.VISIBLE);
        animateView(restartButton);
        animateView(winnerTextView);
    }


    /**
     * Animate the  view
     * @param view : the  view to animate
     */
    private void animateView(View view) {
        // Créez des animations pour l'apparition (fade in) et l'agrandissement (scale up)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(1000); // 1 seconde

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        scaleX.setDuration(1000); // 1 seconde

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        scaleY.setDuration(1000); // 1 seconde

        // Combinez les animations dans un AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, scaleX, scaleY);
        animatorSet.start();
    }


    /**
     * Animate the image view rotation
     * @param imageView : the image view to animate
     * @param duration : the duration of the animation
     */
    private void animateImageViewRotation(ImageView imageView, int duration) {
        // Créez des animations pour l'apparition (fade in) et l'agrandissement (scale up)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
        scaleX.setDuration(duration);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f);
        scaleY.setDuration(duration);

        // Créez une animation de rotation
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotate.setDuration(duration);

        // Combinez les animations dans un AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, scaleX, scaleY, rotate);
        animatorSet.start();
    }

    /**
     * Display the interdiction indicator
     */
    private void displayInterdiction() {
        View view = binding.gridContainer;
        Animation shake = AnimationUtils.loadAnimation(this.getContext(), R.anim.shake);
        view.startAnimation(shake);
    }

    /**
     * Display the current turn
     */
    private void displayTurn() {
        char currentPlayer = game.getColorOfCurrentPlayer();
        TextView turn;
        ImageView skip = null;
        binding.blueSkip.setVisibility(View.GONE);
        binding.orangeSkip.setVisibility(View.GONE);

        if(this.aiTurn && this.removeTurnPlayer1.isEmpty()){
            binding.blueTurn.setVisibility(View.INVISIBLE);
            binding.orangeTurn.setVisibility(View.INVISIBLE);
            return;
        }

        if (currentPlayer == 'B') {
            turn = binding.blueTurn;
            skip = binding.blueSkip;
            binding.orangeTurn.setVisibility(View.INVISIBLE);
        } else {
            turn = binding.orangeTurn;
            skip = binding.orangeSkip;
            binding.blueTurn.setVisibility(View.INVISIBLE);
        }
        turn.setTextSize(70);

        if (this.placeTurn) {
            turn.setText("PLACE");
            skip = null;
        } else if(this.pushTurn) {
            turn.setText("PUSH");
        }

        if(!this.removeTurnPlayer1.isEmpty() || !this.removeTurnPlayer2.isEmpty()){
            turn.setText("REMOVE");
            turn.setTextSize(60);
            skip = null;
        }
        if (skip != null){
            skip.setVisibility(View.VISIBLE);
        }
        if (!grid.isFull()){
            turn.setVisibility(View.VISIBLE);
        }
        if(this.finishGame){
            turn.setVisibility(View.INVISIBLE);
            if (skip != null){
                skip.setVisibility(View.INVISIBLE);
            }
            return;
        }

        animateView(turn);
        animateView(skip);
    }

    /**
     * Skip the push turn
     */
    public void skipPushTurn() {
        handleTurn();
    }

    /**
     * Play AI turn
     */
    private void playAITurn() {

        MinMaxAgent ai = (MinMaxAgent) game.getCurrentPlayer();
        Action action = ai.evaluateAction(grid);

        boolean displayInTerminal = Settings.getInstance().getDisplayInTerminal();
        Grid grid = action.getGrid();

        // Retire les jetons de l'alignement de 5 jetons de l'adversaire
        for (Coordinates removCoords : action.getStartRemove()) {
            grid.removeToken(removCoords);
        }

        displayTokenGrid();

        DelayedTaskUtil.executeWithDelay(500, () -> {
            // Place le jeton sur le plateau
            if (action.getPlacement() != null) {
                grid.placeToken(ai.getColor(), action.getPlacement());
                displayTokenGrid();
            }
        });

        DelayedTaskUtil.executeWithDelay(1000, () -> {
            // Pousse le jeton si une poussée est possible
            if (action.getPush() != null) {
                grid.pushToken(action.getPush(), ai.getColor());
                animateTokenPush(animationVariables);
            }
            checkWin();
        });

        DelayedTaskUtil.executeWithDelay(1500, () -> {
            // Retire les jetons de l'alignement de 5 jetons du joueur
            for (Coordinates removCoords : action.getEndRemove()) {
                grid.removeToken(removCoords);
            }
            this.placeTurn = false;
            this.pushTurn = true;
            displayTokenGrid();
            handleTurn();
        });


    }



    /**
     * Display the grid of tokens in the GridLayout
     */
    private void displayTokenGrid() {
        GridLayout tokenGridLayout = binding.tokenGridLayout;
        tokenGridLayout.removeAllViews();

        int size = this.grid.getSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ImageView cell = new ImageView(getContext());
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = param.height = (getResources().getDisplayMetrics().widthPixels) / size;
                param.columnSpec = GridLayout.spec(i, 1f);
                param.rowSpec = GridLayout.spec(j, 1f);
                cell.setLayoutParams(param);

                Coordinates coordinates = new Coordinates(i, j);
                Token token = this.grid.getToken(coordinates);

                if (token != null) {
                    cell.setClickable(true); // Make the cell clickable
                    cell.setFocusable(true); // Make the cell focusable
                    int final_i = i;
                    int final_j = j;


                    cell.setOnTouchListener(new SwipeListener(getContext(), this, coordinates, cell));

                    if (token.getColor() == 'B') {
                        cell.setImageResource(R.drawable.blue_token);
                    } else if (token.getColor() == 'Y') {
                        cell.setImageResource(R.drawable.yellow_token);
                    }
                }

                tokenGridLayout.addView(cell);
            }
        }
    }



    /**
     * Handle cell click event for removing or pushing a token
     * @param i : x coordinate of the cell
     * @param j : y coordinate of the cell
     */
    public void handleCellClick(int i, int j) {

        if(this.finishGame){
            printTest("The game is finished");
            return;
        }

        if (this.aiTurn && removeTurnPlayer1.isEmpty()) {
            printTest("It's not your turn");
            return;
        }


        Coordinates coordinates = new Coordinates(i, j);
        PlayerAgent currentPlayer = (PlayerAgent) game.getCurrentPlayer();
        Token token = this.grid.getToken(coordinates);
        HashMap<List<Coordinates>, Integer> removeTurn = new HashMap<>();

        if (currentPlayer.equals(game.getPlayer1())) {
            removeTurn = this.removeTurnPlayer1;
        } else if (currentPlayer.equals(game.getPlayer2())) {
            removeTurn = this.removeTurnPlayer2;
        }



        if (!removeTurn.isEmpty()) {
            if (token == null) {
                printTest("This cell is empty");
                return;
            }

            char expectedColor = currentPlayer.equals(game.getPlayer1()) ? 'B' : 'Y';
            if (token.getColor() != expectedColor) {
                printTest("This cell is not yours");
                displayInterdiction();
                return;
            }

            List<Coordinates> currentAlignment = null;

            for (List<Coordinates> alignment : removeTurn.keySet()) {
                // Vérifiez si l'alignement contient les coordonnées
                if (alignment.contains(coordinates)) {
                    currentAlignment = alignment;
                    break;

                    }
            }

            if (currentAlignment == null) {
                printTest("You can't remove this token");
                displayInterdiction();
                return;
            }

            int nbTokensToRemove = removeTurn.get(currentAlignment);
            removeTurn.put(currentAlignment, nbTokensToRemove - 1);

            if (removeTurn.get(currentAlignment) == 0) {
                removeTurn.remove(currentAlignment);
            }

            this.grid.removeToken(coordinates);

            if (currentPlayer.equals(game.getPlayer1())) {
                this.removeTurnPlayer1 = removeTurn;
            } else {
                this.removeTurnPlayer2 = removeTurn;
            }

            if (removeTurn.isEmpty()) {
                if(firstRemovePlayer == game.getColorOfCurrentPlayer()){
                    game.switchPlayer();
                    changeColorToCurrentPlayer();
                }

                displayTurn();
            }
            displayTokenGrid();
        }
    }

    /**
     * Make a push for the current player
     * @param directions : the directions to push the token
     */
    public void makePushCurrentPlayer(int[] directions) {
        if(!this.pushTurn || this.aiTurn){
            printTest("You can't push now");
            return;
        }
        PushAction pushAction = new PushAction(this.coordinatesToPush, directions);

        boolean validPush = grid.isPushValid(pushAction, game.getColorOfCurrentPlayer());

        if (!validPush) {
            printTest("Invalid push");
            displayInterdiction();
            return;
        }

        grid.pushToken(pushAction, game.getColorOfCurrentPlayer());
        animateTokenPush(animationVariables);
        displayBorderGrid();
    }

    /**
     * Animate the token push when a player pushes a token in a direction
     * @param animationVariables : the variables needed for the animation
     *                           (the tokens to animate, the number of cases to push, the directions)
     */
    private void animateTokenPush(AnimationVariables animationVariables) {
        // Récupérer la grille de jetons
        GridLayout tokenGridLayout = binding.tokenGridLayout;

        // Récupérer les jetons déplacés
        HashMap<Coordinates, Token> tokenMoved = animationVariables.getTokensToAnimate();

        // Récupérer les arguments de l'animation
        int nbCaseToPush = animationVariables.getNbCasesToPush();
        int directX = animationVariables.getDirections()[0];
        int directY = animationVariables.getDirections()[1];

        // Appliquer l'animation aux jetons déplacés
        for (Coordinates oldCoordinates : tokenMoved.keySet()) {
            int oldIndex = oldCoordinates.getX() * grid.getSize() + oldCoordinates.getY();
            ImageView cell = (ImageView) tokenGridLayout.getChildAt(oldIndex);

            // Appliquer l'animation
            int translationX = (directX * cell.getWidth() * nbCaseToPush);
            int translationY = (directY * cell.getHeight() * nbCaseToPush);

            Animation animation = new TranslateAnimation(0, translationX, 0, translationY);
            animation.setDuration(500); // durée de l'animation
            animation.setFillAfter(true); // l'élément reste à la position finale après l'animation
            cell.startAnimation(animation);
        }

        if (!this.aiTurn) {
            DelayedTaskUtil.executeWithDelay(500, this::handleTurn);
        }
    }


    /**
     * Set the coordinates to push
     * @param coordinatesToBePushed
     *          the coordinates to push
     */
     public void setCoordinatesToPush(Coordinates coordinatesToBePushed) {
     this.coordinatesToPush = coordinatesToBePushed;
     }

    /**
     * Print a test message using Toast
     * @param msg : the message to display
     * */
    private void printTest(String msg) {
        if (allowToPrintMessages) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
