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
import myPackage.*;

//test
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment {
    private FragmentGridBinding binding;

    private Grid grid;
    private Grid oldGrid;
    private Game game;
    private char choice;
    private SwipeListener swipeListener;
    private boolean pushTurn = false;
    private boolean placeTurn = true;

    private int removeTurnPlayer1 = 0;
    private int removeTurnPlayer2 = 0;

    private boolean finishGame = false;

    private List<Coordinates> alignmentToRemovePlayer1 = new ArrayList<>();
    private List<Coordinates> alignmentToRemovePlayer2 = new ArrayList<>();

    private boolean aiTurn = false;

    private Coordinates coordinatesToPush = null;

    public GridFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GridFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        Settings.getInstance(true, false);
        this.grid = new Grid();
        
        this.oldGrid = new Grid();
        this.game = new Game(this.grid);
        this.game.start(this.choice);
        changeColorToCurrentPlayer();
        updateDisplayScore();

        if (this.choice == '3') {
            this.aiTurn = true;
        }

        displayTokenGrid();
        displayBorderGrid(-1, -1);
        binding.mainMenu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                        navigateToHomeFragment();
                   }
                });
        swipeListener = new SwipeListener(getContext(), this);
        binding.wholeScreen.setOnTouchListener(swipeListener);
        binding.tokenGridLayout.setOnTouchListener(swipeListener);
        if (choice == '3' ){
            DelayedTaskUtil.executeWithDelay(500, this::playAITurn);
        }
        return binding.getRoot();

    }
    private void navigateToHomeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HomePageFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Display the grid of borders in the GridLayout
     * @param highlightX : x coordinate of the cell to highlight
     * @param highlightY : y coordinate of the cell to highlight
     */
    private void displayBorderGrid(int highlightX, int highlightY) {
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
                if (grid.getGrid().containsKey(new Coordinates(i, j)) && i == highlightX && j == highlightY){
                    if (grid.getGrid().get(new Coordinates(i, j)).getColor() == 'B') {
                        cell.setBackgroundResource(R.drawable.cell_border_blue);
                    }
                    else {
                        cell.setBackgroundResource(R.drawable.cell_border_yellow);
                    }
                }
                else {
                    cell.setBackgroundResource(R.drawable.cell_border); // Add a drawable for cell border
                }

                final int finalI = i;
                final int finalJ = j;
                cell.setClickable(true); // Make the cell clickable
                cell.setFocusable(true); // Make the cell focusable
                cell.setOnClickListener(v -> handleCellClickPlace(finalI, finalJ));
                borderGridLayout.addView(cell);
            }
        }
    }

    private void handleCellClickPlace(int i, int j) {
        Coordinates coordinates = new Coordinates(i, j);
        if(this.removeTurnPlayer1 > 0 || this.removeTurnPlayer2 > 0){
            Toast.makeText(getContext(), "You can't place now need to remove token before", Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.grid.getToken(coordinates) == null && placeTurn && !aiTurn) {
            try {
                this.grid.placeToken(game.getColorOfCurrentPlayer(), coordinates);
                handleTurn();
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (!placeTurn){
                Toast.makeText(getContext(), "You can't place now", Toast.LENGTH_SHORT).show();
            }
            else if (aiTurn) {
                Toast.makeText(getContext(), "It's not your turn", Toast.LENGTH_SHORT).show();
            }
            else {
            Toast.makeText(getContext(), "This cell is already occupied", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void changeColorToCurrentPlayer(){
        if (game.getColorOfCurrentPlayer() == 'B') {
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#385ea0"));
        }
        else {
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#ce9744"));
        }
    }


    public void handleTurn() {
        if (!this.finishGame) {
            if (this.removeTurnPlayer1 == 0 && this.removeTurnPlayer2 == 0) {
                if (this.placeTurn) {
                    this.pushTurn = true;
                    this.placeTurn = false;
                }

                else if (this.pushTurn) {
                    this.pushTurn = false;
                    this.placeTurn = true;
                    checkWin();
                    if (this.choice == '2'){
                        this.aiTurn = !this.aiTurn;
                    }
                    if (this.removeTurnPlayer1 == 0 && this.removeTurnPlayer2 == 0){
                        game.switchPlayer();
                        changeColorToCurrentPlayer();
                    }

                }
            }

            displayTokenGrid();
            displayBorderGrid( -1, -1);
            if (this.aiTurn && this.removeTurnPlayer1 == 0 && this.removeTurnPlayer2 == 0) {
                DelayedTaskUtil.executeWithDelay(500, this::playAITurn);
            }
        }

    }

    public void checkWin() {
        int gameState = game.updateScore();
        updateDisplayScore();
        if (gameState == 0) {
            Toast.makeText(getContext(), "Blue wins", Toast.LENGTH_SHORT).show();
            this.finishGame = true;
            displayWinner("Blue");
        } else if (gameState == 1) {
            Toast.makeText(getContext(), "Yellow wins", Toast.LENGTH_SHORT).show();
            this.finishGame = true;
            displayWinner("Orange");
        }
        else {
            int[] order = {0, 1}; // Player 1 first

            if (game.getPlayer2().equals(game.getCurrentPlayer())) {
                order[0] = 1;
                order[1] = 0;
            }

            for (int i : order) {
                for (List<Coordinates> alignment : grid.getAlignmentsOfFive().get(i)) {
                    try {
                        if (i == 0){
                            if (choice == '3'){
                                game.getPlayer1().removeTwoTokens(alignment);
                            }
                            else {
                                this.removeTurnPlayer1 += 2;
                                this.alignmentToRemovePlayer1.addAll(alignment);
                            }
                        }
                        else{
                            if (choice == '3' || choice == '2'){
                                game.getPlayer2().removeTwoTokens(alignment);
                            }
                            else{
                                this.removeTurnPlayer2 += 2;
                                this.alignmentToRemovePlayer2.addAll(alignment);
                            }
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateDisplayScore() {
        TextView scoreView = binding.scoreBoard;
        int[] scores = game.getScore();
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
        animateWinnerTextView();
        DelayedTaskUtil.executeWithDelay(10000, this::navigateToHomeFragment);

    }

    private void animateWinnerTextView() {
        TextView winnerTextView = binding.winnerTextView;
        // Créez des animations pour l'apparition (fade in) et l'agrandissement (scale up)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(winnerTextView, "alpha", 0f, 1f);
        fadeIn.setDuration(1000); // 1 seconde

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(winnerTextView, "scaleX", 0f, 1f);
        scaleX.setDuration(1000); // 1 seconde

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(winnerTextView, "scaleY", 0f, 1f);
        scaleY.setDuration(1000); // 1 seconde

        // Combinez les animations dans un AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, scaleX, scaleY);
        animatorSet.start();
    }

    public void playAITurn() {
        game.getCurrentPlayer().placeToken();
        displayTokenGrid();
        oldGrid.setGrid(grid.copyGrid());

        //wait for 1 second
        DelayedTaskUtil.executeWithDelay(1000, () -> {
            AnimationVariables animationVariables = game.getCurrentPlayer().pushToken();
            this.pushTurn = true;
            this.placeTurn = false;
            animateTokenPush(animationVariables);
        });
    }

    /**
     * Display the grid of tokens in the GridLayout
     *
     */
    public void displayTokenGrid() {
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
                    cell.setOnClickListener(v -> handleCellClick(final_i, final_j, cell));


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

    public void handleCellClick(int i, int j, ImageView cell) {
        Coordinates coordinates = new Coordinates(i, j);

        if (this.removeTurnPlayer1 > 0 && game.getCurrentPlayer().equals(game.getPlayer1())) {
            if (this.grid.getToken(coordinates) != null && this.grid.getToken(coordinates).getColor() == 'B') {
                if (this.alignmentToRemovePlayer1.contains(coordinates)) {
                    this.alignmentToRemovePlayer1.remove(coordinates);
                    this.grid.removeToken(coordinates);
                    this.removeTurnPlayer1 -= 1;
                    if (this.removeTurnPlayer1 == 0) {
                        this.alignmentToRemovePlayer1.clear();
                        game.switchPlayer();
                        changeColorToCurrentPlayer();
                        if (this.choice == '2'){
                            handleTurn();
                        }
                    }
                    displayTokenGrid();
                }
                else {
                    Toast.makeText(getContext(), "This cell is not in an alignment of 5", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                Toast.makeText(getContext(), "This cell is not yours", Toast.LENGTH_SHORT).show();
            }
        }
        else if (this.removeTurnPlayer2 > 0 && game.getCurrentPlayer().equals(game.getPlayer2())){
            if (this.grid.getToken(coordinates) != null && this.grid.getToken(coordinates).getColor() == 'Y') {
                if (this.alignmentToRemovePlayer2.contains(coordinates)) {
                    this.alignmentToRemovePlayer2.remove(coordinates);
                    this.grid.removeToken(coordinates);
                    this.removeTurnPlayer2 -= 1;
                    if (this.removeTurnPlayer2 == 0) {
                        this.alignmentToRemovePlayer2.clear();
                        game.switchPlayer();
                        changeColorToCurrentPlayer();
                    }
                    displayTokenGrid();
                }
                else {
                    Toast.makeText(getContext(), "This cell is not in an alignment of 5", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                Toast.makeText(getContext(), "This cell is not yours", Toast.LENGTH_SHORT).show();
            }
        }


        if (this.grid.getToken(coordinates) != null && pushTurn && !aiTurn) {
            if (this.grid.getToken(coordinates).getColor() != game.getColorOfCurrentPlayer()) {
                Toast.makeText(getContext(), "This cell is not yours", Toast.LENGTH_SHORT).show();
                return;
            }
            displayBorderGrid(i, j);
            cell.setOnTouchListener(swipeListener);

            swipeListener.setWaitSlide(true);

            this.coordinatesToPush = coordinates;
        }
        else {
            if (aiTurn) {
                Toast.makeText(getContext(), "It's not your turn", Toast.LENGTH_SHORT).show();
            }
            else if (!pushTurn) {
                Toast.makeText(getContext(), "You can't push now", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "This cell is empty", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void makePushCurrentPlayer(int[] directions) {
        oldGrid.setGrid(grid.copyGrid());
        try {
            AnimationVariables animationVariables = this.grid.pushToken(this.game.getColorOfCurrentPlayer(), this.coordinatesToPush, directions);
            animateTokenPush(animationVariables);
            displayBorderGrid(-1, -1);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            displayBorderGrid(-1, -1);
        }


    }

    /**
     * Animate the token push when a player pushes a token in a direction
     * @param animationVariables : the variables needed for the animation
     *                           (the tokens to animate, the number of cases to push, the directions)
     *
     */
    public void animateTokenPush(AnimationVariables animationVariables) {
        // Récupérer les positions des jetons avant et après le déplacement
        HashMap<Coordinates, Token> oldPositions = oldGrid.getGrid();
        HashMap<Coordinates, Token> newPositions = grid.getGrid();
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

        DelayedTaskUtil.executeWithDelay(500, this::handleTurn);
    }

    public void printTest(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}