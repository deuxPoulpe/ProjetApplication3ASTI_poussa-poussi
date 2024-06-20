package com.example.poussapoussi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.poussapoussi.databinding.FragmentHomePageBinding;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentHomePageBinding binding;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final long ANIMATION_DURATION = 1000;
    private AnimatorSet animatorSet;

    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
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
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the game name to animate
        TextView gameNameTextView = view.findViewById(R.id.GameName);
        startAnimation(gameNameTextView);

        binding.player1.setOnClickListener(v -> {
            navigateToGridFragment('2');
        });

        binding.player2.setOnClickListener(v -> {
            navigateToGridFragment('1');
        });

        binding.iavsia.setOnClickListener(v -> {
            navigateToGridFragment('3');
        });
        binding.rules.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, RulesFragment.newInstance(mParam1, mParam2))
                                    .addToBackStack(null)
                                    .commit();
                        }

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    private void navigateToGridFragment(char choice) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, GridFragment.newInstance(choice))
                    .addToBackStack(null)
                    .commit();
        }
    }

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


}