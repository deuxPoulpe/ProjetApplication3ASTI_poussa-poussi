package com.example.poussapoussi;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.util.Log;
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
    private int[] directionList;

    private Grid grid;
    private Game game;

    private char choice;

    private boolean pushTurn = false;
    private boolean placeTurn = true;

    private boolean aiTurn = false;

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
        this.grid = new Grid();
        this.game = new Game(this.grid);
        this.game.start(this.choice);
        changeColorToCurrentPlayer();

        if (this.choice == '3') {
            this.aiTurn = true;
        }

        displayBorderGrid();
        displayTokenGrid();
        binding.mainMenu.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                        navigateToHomeFragment();
                   }
                });
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

    private void handleCellClickPlace(int i, int j) {
        Coordinates coordinates = new Coordinates(i, j);
        if (this.grid.getToken(coordinates) == null && placeTurn && !aiTurn) {
            try {
                this.grid.placeToken(game.getColorOfCurrentPlayer(), coordinates);
                handleTurn();
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), "This cell is already occupied", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleCellPush(int i, int j) {
        Coordinates coordinates = new Coordinates(i, j);
        Token pushedToken = this.grid.getToken(coordinates);

        if (this.pushTurn && !aiTurn) {


            binding.wholeScreen.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float initialX = 0, initialY = 0;
                    int action = event.getActionMasked();

                    switch (action) {

                        case MotionEvent.ACTION_DOWN:
                            initialX = event.getX();
                            initialY = event.getY();


                            // Log.d(TAG, "Action was DOWN");
                            break;

                        case MotionEvent.ACTION_MOVE:

                            //Log.d(TAG, "Action was MOVE");
                            break;

                        case MotionEvent.ACTION_UP:
                            float finalX = event.getX();
                            float finalY = event.getY();


                            //Log.d(TAG, "Action was UP");

                            if (initialX < finalX) {
                                // Log.d(TAG, "Left to Right swipe performed");
                                directionList[0] = 1;
                                directionList[1] = 0;
                            }

                            if (initialX > finalX) {
                                // Log.d(TAG, "Right to Left swipe performed");
                                directionList[0] = -1;
                                directionList[1] = 0;
                            }

                            if (initialY < finalY) {
                                // Log.d(TAG, "Up to Down swipe performed");
                                directionList[0] = 0;
                                directionList[1] = -1;
                            }

                            if (initialY > finalY) {
                                // Log.d(TAG, "Down to Up swipe performed");
                                directionList[0] = 0;
                                directionList[1] = 1;
                            }

                            break;

                        case MotionEvent.ACTION_CANCEL:
                            //Log.d(TAG,"Action was CANCEL");
                            break;

                        case MotionEvent.ACTION_OUTSIDE:
                            // Log.d(TAG, "Movement occurred outside bounds of current screen element");
                            break;
                    }

                    return true;
                }

            });

            this.grid.pushToken(pushedToken.getColor(), coordinates, directionList);
            handleTurn();


        }
        else {
            Toast.makeText(getContext(), "You can't push this token", Toast.LENGTH_SHORT).show();
        }
}



    public void changeColorToCurrentPlayer(){
        if (game.getColorOfCurrentPlayer() == 'B') {
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#1b4ca0"));
        }
        else {
            binding.wholeScreen.setBackgroundColor(Color.parseColor("#d17e02"));
        }
    }


    public void handleTurn() {
        if (this.placeTurn) {
            if (this.aiTurn) {
                //FAIRE JOUER L'IA ICI
                // A COMPLETER
            }
            this.pushTurn = true;
            this.placeTurn = false;
        }

        else if (this.pushTurn) {
            if (this.aiTurn) {
                //FAIRE JOUER L'IA ICI
                // A COMPLETER
            }
            this.pushTurn = false;
            this.placeTurn = true;
            //verification si le joueur marque un point et a gagné
            if (this.choice == '2'){
                this.aiTurn = !this.aiTurn;
            }
            game.switchPlayer();
            changeColorToCurrentPlayer();
        }

        displayTokenGrid();

    }


    @SuppressLint("ClickableViewAccessibility")
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
                    cell.setOnClickListener(v -> handleCellPush(final_i, final_j));


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

    public void printTest(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void draggingDirection(int i, int j) {
        //binding.wholeScreen.onDragEvent();
    }


    public void updateGrid(Grid newGrid) {
        this.grid = newGrid;
        displayTokenGrid();
    }




}