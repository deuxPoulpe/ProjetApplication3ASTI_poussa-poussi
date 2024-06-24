package com.example.poussapoussi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.poussapoussi.databinding.FragmentSelectBinding;

import java.util.zip.Inflater;

import UtilsPackage.DelayedTaskUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectFragment extends Fragment {


    private FragmentSelectBinding binding;

    private GridFragment gridFragment;
    private HomePageFragment homePageFragment;

    private static final long ANIMATION_DURATION = 1000;
    private AnimatorSet animatorSet;
    
    private boolean settingsDisplayed = false; 
    
    int orangeDifficulty = 1;
    int blueDifficulty = 1;


    public SelectFragment() {
        // Required empty public constructor
    }

    public SelectFragment(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectFragment.
     */
    public static SelectFragment newInstance() {
        SelectFragment fragment = new SelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the game name to animate
        TextView gameNameTextView = view.findViewById(R.id.GameName);
        startAnimation(gameNameTextView);
        
        // Set the seekbar to handle the difficulty
        handleSeekBar();

        // Set the buttons to navigate to the grid fragment for 1 player vs ai
        binding.player1.setOnClickListener(v -> {
            navigateToGridFragment('2');
        });

        // Set the buttons to navigate to the grid fragment for 2 players
        binding.player2.setOnClickListener(v -> {
            navigateToGridFragment('1');
        });

        // Set the buttons to navigate to the grid fragment for ai vs ai
        binding.iavsia.setOnClickListener(v -> {
            navigateToGridFragment('3');
        });

        // Set the back button to navigate to the home page fragment
        binding.back.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homePageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        
        binding.settings.setOnClickListener(v -> {
            displaySettings();
        });

    }

    /**
     * This method is used to navigate to the grid fragment
     * @param choice the choice of the user
     */
    private void navigateToGridFragment(char choice) {
        if (getActivity() != null) {
            gridFragment = new GridFragment(choice, homePageFragment, orangeDifficulty, blueDifficulty);
            homePageFragment.setGridFragment(gridFragment);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, gridFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    /**
     * This method is used to display the settings
     * It will display the settings if they are not displayed and hide them if they are displayed
     * It will also animate the views
     */
    private void displaySettings() {

        //disable the buttons while tha animation is running
        binding.settings.setEnabled(false);
        DelayedTaskUtil.executeWithDelay(1000, () -> binding.settings.setEnabled(true));
        
        if (settingsDisplayed) {
            invertAnimationView(binding.linearLayoutSettings);
            DelayedTaskUtil.executeWithDelay(1000, () -> binding.linearLayoutSettings.setVisibility(View.GONE));
            binding.player1.setVisibility(View.VISIBLE);
            animateView(binding.player1);
            binding.player2.setVisibility(View.VISIBLE);
            animateView(binding.player2);
            binding.iavsia.setVisibility(View.VISIBLE);
            animateView(binding.iavsia);
            settingsDisplayed = false;
        }
        else {
            binding.linearLayoutSettings.setVisibility(View.VISIBLE);
            animateView(binding.linearLayoutSettings);
            invertAnimationView(binding.player1);
            DelayedTaskUtil.executeWithDelay(1000, () -> binding.player1.setVisibility(View.GONE));
            invertAnimationView(binding.player2);
            DelayedTaskUtil.executeWithDelay(1000, () -> binding.player2.setVisibility(View.GONE));
            invertAnimationView(binding.iavsia);
            DelayedTaskUtil.executeWithDelay(1000, () -> binding.iavsia.setVisibility(View.GONE));
            settingsDisplayed = true;
        }
               
    }

    /**
     * This method is used to handle the seekbar
     * It will update the difficulty of the AI
     * It will also update the textview displaying the difficulty
     */
    private void handleSeekBar() {
        // Handle the seekbar
        binding.seekBarOrange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                orangeDifficulty = progress;
                binding.seekBarValueOrange.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
        binding.seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blueDifficulty = progress;
                binding.seekBarValueBlue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    /**
     * This method is used to start the animation of the textview
     * @param textView the textview to animate
     */
    private void startAnimation(TextView textView) {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(textView, "rotation", -15f, 15f);
        rotateAnimator.setDuration(ANIMATION_DURATION);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.2f);
        scaleXAnimator.setDuration(ANIMATION_DURATION);
        scaleXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.2f);
        scaleYAnimator.setDuration(ANIMATION_DURATION);
        scaleYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(textView, "translationX", -20f, 20f);
        translationXAnimator.setDuration(ANIMATION_DURATION);
        translationXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translationXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        translationXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(textView, "translationY", -10f, 10f);
        translationYAnimator.setDuration(ANIMATION_DURATION);
        translationYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translationYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        translationYAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimator, scaleXAnimator, scaleYAnimator, translationXAnimator, translationYAnimator);
        animatorSet.start();
    }

    /**
     * This method is used to animate a view
     * @param view the view to animate
     */
    private void animateView(View view) {
        // Créez des animations pour l'apparition (fade in) et l'agrandissement (scale up)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1f);
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
     * This method is used to invert the animation of a view
     * @param view the view to animate
     */
    private void invertAnimationView(View view) {
        // Créez des animations pour la disparition (fade out) et la réduction (scale down)
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(1000); // 1 seconde

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        scaleX.setDuration(1000); // 1 seconde

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
        scaleY.setDuration(1000); // 1 seconde

        // Combinez les animations dans un AnimatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeOut, scaleX, scaleY);
        animatorSet.start();
    }
}