package com.lacolinares.mazegame;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class MainGame extends AppCompatActivity {

    private MazeView mazeView;
    private LinearLayout main_container;
    private TextView txtScore;
    private TextView txtTimer;

    private Context mContext;

    public MainGame() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        mContext = MainGame.this;

        //initialize the views
        main_container = findViewById(R.id.main_container);
        txtScore = findViewById(R.id.txt_score);
        txtTimer = findViewById(R.id.txt_timer);

        //initialize the game
        mazeView = new MazeView(mContext, null, txtScore, txtTimer, MainGame.this);
        main_container.addView(mazeView);
    }

    @Override
    public void onBackPressed() {
        //pause the time
        mazeView.pauseTimer();

        //show warning message
        final SweetAlertDialog sDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        sDialog.setTitleText(getResources().getString(R.string.app_name));
        sDialog.setContentText("Are you sure you want to exit the game?");
        sDialog.setConfirmText("Yes");
        sDialog.setCancelText("No");
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //go back to main menu
                int score = mazeView.getCurrentScore();

                int highScore = ScoreUtil.getHighScore(mContext);

                if (score > highScore) {
                    ScoreUtil.setScore(mContext, score);
                }
                exit();
            }
        });
        sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //just close the message and resume the timer
                sDialog.dismissWithAnimation();
                mazeView.resumeTimer();
            }
        });
        sDialog.show();
    }

    public void exit() {
        startActivity(new Intent(mContext, MainMenu.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mazeView.pauseTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mazeView.resumeTimer();
    }
}
