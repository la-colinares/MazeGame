package com.lacolinares.mazegame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    private Button btnStart;
    private TextView txtHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnStart = findViewById(R.id.btn_start);
        txtHighScore = findViewById(R.id.txt_high_score);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, MainGame.class));
                finish();
            }
        });

        int score = ScoreUtil.getHishScore(MainMenu.this);
        txtHighScore.setText("High Score: " + score);

    }

}
