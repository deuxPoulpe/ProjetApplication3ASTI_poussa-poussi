package com.example.poussapoussi;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.poussapoussi.databinding.FragmentRulesBinding;
import UtilsPackage.SpinnerAdapter;

public class RulesFragment extends Fragment {

    private FragmentRulesBinding binding;

    private TextView rulesTitle, preamble, goal, goalDetail, gameStart, gameStartDetail, turn, turnDetail, turn_detail_attention_title, turn_detail_attention;
    private HomePageFragment homePageFragment;

    public RulesFragment() {
        // Required empty public constructor
    }

    public RulesFragment(HomePageFragment homePageFragment) {
        this.homePageFragment = homePageFragment;
    }

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
        binding = FragmentRulesBinding.inflate(inflater, container, false);

        initViews();
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

    private void initViews() {
        rulesTitle = binding.rulesTitle;
        preamble = binding.preambule;
        goal = binding.goal;
        goalDetail = binding.goalDetail;
        gameStart = binding.gameStart;
        gameStartDetail = binding.gameStartDetail;
        turn = binding.turn;
        turnDetail = binding.turnDetail;
        turn_detail_attention_title = binding.turnDetailAttentionTitle;
        turn_detail_attention = binding.turnDetailAttention;

        Spinner languageSpinner = binding.languageSpinner;

        String[] languages = getResources().getStringArray(R.array.languages);
        int[] flags = {
                R.drawable.ic_flag_english,
                R.drawable.ic_flag_french,
                R.drawable.ic_flag_spanish,
                R.drawable.ic_flag_italian,
                R.drawable.ic_flag_german,
                R.drawable.ic_flag_danish,
                R.drawable.ic_flag_polish,
                R.drawable.ic_flag_japanese,
                R.drawable.ic_flag_chinese,
                R.drawable.ic_flag_korean,
                R.drawable.ic_flag_arabic,
                R.drawable.ic_flag_vietnamese
        };

        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), languages, flags);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateRules(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Set default rules to English
        updateRules(0);
    }

    private void updateRules(int language) {
        switch (language) {
            case 0: // English
                rulesTitle.setText(R.string.rules_title_en);
                preamble.setText(R.string.preamble_en);
                goal.setText(R.string.goal_en);
                goalDetail.setText(R.string.goal_detail_en);
                gameStart.setText(R.string.game_start_en);
                gameStartDetail.setText(R.string.game_start_detail_en);
                turn.setText(R.string.turn_en);
                turnDetail.setText(R.string.turn_detail_en);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_en);
                turn_detail_attention.setText(R.string.turn_detail_attention_en);
                break;
            case 1: // French
                rulesTitle.setText(R.string.rules_title_fr);
                preamble.setText(R.string.preamble_fr);
                goal.setText(R.string.goal_fr);
                goalDetail.setText(R.string.goal_detail_fr);
                gameStart.setText(R.string.game_start_fr);
                gameStartDetail.setText(R.string.game_start_detail_fr);
                turn.setText(R.string.turn_fr);
                turnDetail.setText(R.string.turn_detail_fr);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_fr);
                turn_detail_attention.setText(R.string.turn_detail_attention_fr);
                break;
            case 2: // Spanish
                rulesTitle.setText(R.string.rules_title_es);
                preamble.setText(R.string.preamble_es);
                goal.setText(R.string.goal_es);
                goalDetail.setText(R.string.goal_detail_es);
                gameStart.setText(R.string.game_start_es);
                gameStartDetail.setText(R.string.game_start_detail_es);
                turn.setText(R.string.turn_es);
                turnDetail.setText(R.string.turn_detail_es);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_es);
                turn_detail_attention.setText(R.string.turn_detail_attention_es);
                break;
            // Add cases for other languages here
            case 3: // Italian
                rulesTitle.setText(R.string.rules_title_it);
                preamble.setText(R.string.preamble_it);
                goal.setText(R.string.goal_it);
                goalDetail.setText(R.string.goal_detail_it);
                gameStart.setText(R.string.game_start_it);
                gameStartDetail.setText(R.string.game_start_detail_it);
                turn.setText(R.string.turn_it);
                turnDetail.setText(R.string.turn_detail_it);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_it);
                turn_detail_attention.setText(R.string.turn_detail_attention_it);
                break;
            case 4: // German
                rulesTitle.setText(R.string.rules_title_de);
                preamble.setText(R.string.preamble_de);
                goal.setText(R.string.goal_de);
                goalDetail.setText(R.string.goal_detail_de);
                gameStart.setText(R.string.game_start_de);
                gameStartDetail.setText(R.string.game_start_detail_de);
                turn.setText(R.string.turn_de);
                turnDetail.setText(R.string.turn_detail_de);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_de);
                turn_detail_attention.setText(R.string.turn_detail_attention_de);
                break;
            case 5: // Danish
                rulesTitle.setText(R.string.rules_title_da);
                preamble.setText(R.string.preamble_da);
                goal.setText(R.string.goal_da);
                goalDetail.setText(R.string.goal_detail_da);
                gameStart.setText(R.string.game_start_da);
                gameStartDetail.setText(R.string.game_start_detail_da);
                turn.setText(R.string.turn_da);
                turnDetail.setText(R.string.turn_detail_da);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_da);
                turn_detail_attention.setText(R.string.turn_detail_attention_da);
                break;
            case 6: // Polish
                rulesTitle.setText(R.string.rules_title_pl);
                preamble.setText(R.string.preamble_pl);
                goal.setText(R.string.goal_pl);
                goalDetail.setText(R.string.goal_detail_pl);
                gameStart.setText(R.string.game_start_pl);
                gameStartDetail.setText(R.string.game_start_detail_pl);
                turn.setText(R.string.turn_pl);
                turnDetail.setText(R.string.turn_detail_pl);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_pl);
                turn_detail_attention.setText(R.string.turn_detail_attention_pl);
                break;
            case 7: // Japanese
                rulesTitle.setText(R.string.rules_title_ja);
                preamble.setText(R.string.preamble_ja);
                goal.setText(R.string.goal_ja);
                goalDetail.setText(R.string.goal_detail_ja);
                gameStart.setText(R.string.game_start_ja);
                gameStartDetail.setText(R.string.game_start_detail_ja);
                turn.setText(R.string.turn_ja);
                turnDetail.setText(R.string.turn_detail_ja);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_ja);
                turn_detail_attention.setText(R.string.turn_detail_attention_ja);
                break;
            case 8: // Chinese
                rulesTitle.setText(R.string.rules_title_zh);
                preamble.setText(R.string.preamble_zh);
                goal.setText(R.string.goal_zh);
                goalDetail.setText(R.string.goal_detail_zh);
                gameStart.setText(R.string.game_start_zh);
                gameStartDetail.setText(R.string.game_start_detail_zh);
                turn.setText(R.string.turn_zh);
                turnDetail.setText(R.string.turn_detail_zh);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_zh);
                turn_detail_attention.setText(R.string.turn_detail_attention_zh);
                break;
            case 9: // Korean
                rulesTitle.setText(R.string.rules_title_ko);
                preamble.setText(R.string.preamble_ko);
                goal.setText(R.string.goal_ko);
                goalDetail.setText(R.string.goal_detail_ko);
                gameStart.setText(R.string.game_start_ko);
                gameStartDetail.setText(R.string.game_start_detail_ko);
                turn.setText(R.string.turn_ko);
                turnDetail.setText(R.string.turn_detail_ko);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_ko);
                turn_detail_attention.setText(R.string.turn_detail_attention_ko);
                break;
            case 10: // Arabic
                rulesTitle.setText(R.string.rules_title_ar);
                preamble.setText(R.string.preamble_ar);
                goal.setText(R.string.goal_ar);
                goalDetail.setText(R.string.goal_detail_ar);
                gameStart.setText(R.string.game_start_ar);
                gameStartDetail.setText(R.string.game_start_detail_ar);
                turn.setText(R.string.turn_ar);
                turnDetail.setText(R.string.turn_detail_ar);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_ar);
                turn_detail_attention.setText(R.string.turn_detail_attention_ar);
                break;
            case 11: // Vietnamese
                rulesTitle.setText(R.string.rules_title_vi);
                preamble.setText(R.string.preamble_vi);
                goal.setText(R.string.goal_vi);
                goalDetail.setText(R.string.goal_detail_vi);
                gameStart.setText(R.string.game_start_vi);
                gameStartDetail.setText(R.string.game_start_detail_vi);
                turn.setText(R.string.turn_vi);
                turnDetail.setText(R.string.turn_detail_vi);
                turn_detail_attention_title.setText(R.string.turn_detail_attention_title_vi);
                turn_detail_attention.setText(R.string.turn_detail_attention_vi);
                break;
        }
    }
}
