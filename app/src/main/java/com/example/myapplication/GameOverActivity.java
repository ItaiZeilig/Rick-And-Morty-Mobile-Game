package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameOverActivity extends MyAppCompatActivity {

    private String score;
    private String editTextValue;
    private Button resetBtn;
    private Button scoreBoardBtn;
    private Score userScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        final Bundle extra = getIntent().getExtras();

        resetBtn = findViewById(R.id.resetBtn);
        scoreBoardBtn = findViewById(R.id.scoreboardBtn);

        EditText textInput = findViewById(R.id.nameInput);
        resetBtn.setEnabled(false);
        scoreBoardBtn.setEnabled(false);



        textInput.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                editTextValue = s.toString().trim();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    resetBtn.setEnabled(true);
                    scoreBoardBtn.setEnabled(true);
                }
            }
        });

        if(extra != null){
            this.score = extra.getString(EXT_SCORE);
            TextView result = findViewById(R.id.result);
            result.setText(score);
        }

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GameActivity.class));
                finish();
            }
        });

        scoreBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScore(editTextValue, Integer.parseInt(score));
                Intent showScoreboard = new Intent(getApplicationContext(), ScoreboardActivity.class);
                showScoreboard.putExtra(USER_DATA,userScore);
                showScoreboard.putExtra(ID,DEF_TAG);
                finish();
                startActivity(showScoreboard);
            }
        });
    }

    private void initScore(String name, int score) {
        userScore = new Score();
        userScore.setUserName(name);
        userScore.setScore(score);
    }
}
