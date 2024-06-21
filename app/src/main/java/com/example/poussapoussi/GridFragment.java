package com.example.poussapoussi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import com.example.poussapoussi.databinding.FragmentGridBinding;

import agentsPackage.Agent;
import agentsPackage.MinMaxAgent;
import agentsPackage.PlayerAgent;
import gamePackage.*;
import UtilsPackage.*;
import treeFormationPackage.GridTree;


//test
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment {
    private FragmentGridBinding binding;

    private boolean allowToPrintMessages = false;

    private Grid grid;
    private Game game;
    private char choice;
    private boolean pushTurn = false;
    private boolean placeTurn = true;

    private int removeTurnPlayer1 = 0;
    private int removeTurnPlayer2 = 0;

    private boolean finishGame = false;

    private List<Coordinates> alignmentToRemovePlayer1 = new ArrayList<>();
    private List<Coordinates> alignmentToRemovePlayer2 = new ArrayList<>();

    private boolean aiTurn = false;

    private Coordinates coordinatesToPush = null;
    private HomePageFragment homePageFragment;

    private int orangeAiDifficulty;
    private int blueAiDifficulty;

    private boolean resume = false;

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
        Settings.getInstance(true, false, true);

        initSkipButton();
        initGame();


        binding.mainMenu.setOnClickListener(new View.OnClickListener(){
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
            this.grid = new Grid();
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
                cell.setOnClickListener(v -> handleCellClickPlace(finalI, finalJ, cell));
                borderGridLayout.addView(cell);
            }
        }
    }

    /**
     * Handle cell click event for placing a token
     * @param i : x coordinate of the cell
     * @param j : y coordinate of the cell
     */
    private void handleCellClickPlace(int i, int j, ImageView cell) {
        Coordinates coordinates = new Coordinates(i, j);
        if(this.removeTurnPlayer1 > 0 || this.removeTurnPlayer2 > 0){
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
    }

    /**
     * Handle the end of a turn
     */
    private void handleTurn() {
        if (this.finishGame) {
            endTurn();
            return;
        }

        if (this.removeTurnPlayer1 != 0 || this.removeTurnPlayer2 != 0) {
            displayTokenGrid();
            displayBorderGrid();
            return;
        }


        if (this.placeTurn) {
            this.pushTurn = true;
            this.placeTurn = false;

            displayTurn();
        } else if (this.pushTurn) {
            endTurn();
        }


        displayTokenGrid();
        displayBorderGrid();

        if (this.aiTurn) {
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

        if (this.choice == '2') {
            this.aiTurn = !this.aiTurn;
        }

        checkWin();
        game.switchPlayer();
        changeColorToCurrentPlayer();
        displayTurn();


    }

    /**
     * Check for a win or alignments of five tokens
     */
    private void checkWin() {
        //TODO: check for win

    }

    /**
     * Update the score display
     */
    @SuppressLint("SetTextI18n")
    private void updateDisplayScore() {
        TextView scoreView = binding.scoreBoard;
        //int[] scores = game.getScore();       TODO: get score
        int[] scores = {0, 0};
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
        winnerTextView.setText("Winner: " + winner);

        //change background color of the full screen with opacity
        if (winner.equals("Blue")) {
            binding.winnerBackground.setBackgroundColor(Color.parseColor("#BB385ea0"));
        } else {
            binding.winnerBackground.setBackgroundColor(Color.parseColor("#BBce9744"));
        }

        // Rendez le TextView visible
        winnerTextView.setVisibility(View.VISIBLE);
        animateView(winnerTextView);
        DelayedTaskUtil.executeWithDelay(10000, this::navigateToHomeFragment);
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

        if(this.aiTurn){
            binding.blueTurn.setVisibility(View.INVISIBLE);
            binding.orangeTurn.setVisibility(View.INVISIBLE);
            return;
        }

        if (currentPlayer == 'B') {
            turn = binding.blueTurn;
            binding.orangeTurn.setVisibility(View.INVISIBLE);
        } else {
            turn = binding.orangeTurn;
            binding.blueTurn.setVisibility(View.INVISIBLE);
        }

        if (this.placeTurn) {
            turn.setText("PLACE");
        } else {
            turn.setText("PUSH");
            if (currentPlayer == 'B') {
                skip = binding.blueSkip;
            } else {
                skip = binding.orangeSkip;
            }
        }
        if (skip != null){
            skip.setVisibility(View.VISIBLE);
        }
        turn.setVisibility(View.VISIBLE);
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

        MinMaxAgent agent = (MinMaxAgent) game.getCurrentPlayer();
        AnimationVariables animationVariables = null;

        // Calcule le meilleur coup à jouer
        GridTree root = new GridTree(agent, grid);
        final GridTree finalBestMove = agent.evaluateBestMove(root, agent.getSmartness(), Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        // Phase de retrait 1

        // Pour chaque alignement de 5 jetons du joueur formé par l'adversaire, on retire 2 jetons de l'alignement
        AiRemoveToken(finalBestMove);

        displayTokenGrid();

        // Phase de placement

        // Place le jeton sur le plateau
        grid.placeToken(agent.getColor(), finalBestMove.getPlaceCoordinates());

        displayTokenGrid();


        // Phase de poussée



        PushAction pushAction = finalBestMove.getPushAction();

        // Si le plateau n'est pas plein, on pousse le jeton choisi dans la direction choisie
        if (!grid.isFull() && pushAction != null){
            animationVariables = grid.pushToken(agent.getColor(), finalBestMove.getPushAction().getCoordinates(), finalBestMove.getPushAction().getDirection());
        }

        final AnimationVariables finalAnimationVariables = animationVariables;

        DelayedTaskUtil.executeWithDelay(1000, () -> {

            this.pushTurn = true;
            this.placeTurn = false;
            if (finalAnimationVariables != null) {
                animateTokenPush(finalAnimationVariables);
            }
        });


        // Phase de retrait 2

        DelayedTaskUtil.executeWithDelay(1500, () -> {
            // Pour chaque alignement de 5 jetons formé, on retire 2 jetons de l'alignement
            AiRemoveToken(finalBestMove);
            handleTurn();
        });

    }

    private void AiRemoveToken(GridTree BestMove){
        Set<Coordinates> removCoordSet = BestMove.getRemovCoordinates().get(1);
        if (removCoordSet != null) {
            for (Coordinates removCoords : removCoordSet) {
                grid.removeToken(removCoords);
            }
        }
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
        Coordinates coordinates = new Coordinates(i, j);
        PlayerAgent currentPlayer = (PlayerAgent) game.getCurrentPlayer();
        Token token = this.grid.getToken(coordinates);
        List<Coordinates> alignmentToRemove = null;
        int removeTurn = 0;

        if (currentPlayer.equals(game.getPlayer1())) {
            alignmentToRemove = this.alignmentToRemovePlayer1;
            removeTurn = this.removeTurnPlayer1;
        } else if (currentPlayer.equals(game.getPlayer2())) {
            alignmentToRemove = this.alignmentToRemovePlayer2;
            removeTurn = this.removeTurnPlayer2;
        }

        if (removeTurn > 0) {
            if (token == null) {
                printTest("This cell is empty");
                return;
            }

            char expectedColor = currentPlayer.equals(game.getPlayer1()) ? 'B' : 'Y';
            if (token.getColor() != expectedColor) {
                printTest("This cell is not yours");
                return;
            }

            if (!alignmentToRemove.contains(coordinates)) {
                printTest("This cell is not in an alignment of 5");
                return;
            }

            alignmentToRemove.remove(coordinates);
            this.grid.removeToken(coordinates);
            removeTurn--;

            if (currentPlayer.equals(game.getPlayer1())) {
                this.removeTurnPlayer1 = removeTurn;
            } else {
                this.removeTurnPlayer2 = removeTurn;
            }

            if (removeTurn == 0) {
                alignmentToRemove.clear();
                game.switchPlayer();
                changeColorToCurrentPlayer();
                if (this.choice == '2' && currentPlayer.equals(game.getPlayer1())) {
                    handleTurn();
                }
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
        try {
            AnimationVariables animationVariables = this.grid.pushToken(this.game.getColorOfCurrentPlayer(), this.coordinatesToPush, directions);
            animateTokenPush(animationVariables);
            displayBorderGrid();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("You are trying to push tokens in the same previous position")) {
                displayInterdiction();
            }
            else {
                printTest(e.getMessage());
            }
            displayBorderGrid();
        }
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
