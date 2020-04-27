package com.lacolinares.mazegame;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

        int score = ScoreUtil.getHighScore(MainMenu.this);
        txtHighScore.setText("High Score: " + score);

    }

}
