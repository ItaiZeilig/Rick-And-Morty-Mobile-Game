package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends MyAppCompatActivity {

    private Button controlBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startGame = new Intent(getApplicationContext(), GameActivity.class);
                if(controlBtn.getText() == DRAG_CONTROL)
                    startGame.putExtra(CONTROLS, true);
                else
                    startGame.putExtra(CONTROLS, false);
                startActivity(startGame);
            }
        });

        Button scoreboardBtn = findViewById(R.id.scoreboardBtn);
        scoreboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showScoreboard = new Intent(getApplicationContext(), ScoreboardActivity.class);
                showScoreboard.putExtra(ID,MAIN_TAG);
                startActivity(showScoreboard);
            }
        });
        this.controlBtn = findViewById(R.id.controlBtn);
        this.controlBtn.setText(DRAG_CONTROL);
        this.controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlBtn.getText() == DRAG_CONTROL)
                    controlBtn.setText(SENSOR_CONTROL);
                else
                    controlBtn.setText(DRAG_CONTROL);
            }
        });


    }
}
