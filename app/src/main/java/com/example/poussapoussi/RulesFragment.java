package com.example.poussapoussi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poussapoussi.databinding.FragmentRulesBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RulesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentRulesBinding binding;

    
    private HomePageFragment homePageFragment;
    // TODO: Rename and change types of parameters


    public RulesFragment() {
        // Required empty public constructor
    }
    
    public RulesFragment(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RulesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RulesFragment newInstance() {
        RulesFragment fragment = new RulesFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentRulesBinding.inflate(inflater, container, false);
        binding.exit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, homePageFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }

                    }
                });
        return binding.getRoot();
    }
}