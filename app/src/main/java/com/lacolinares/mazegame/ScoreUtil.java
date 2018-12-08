package com.lacolinares.mazegame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Colinares on 12/8/2018.
 */

public class ScoreUtil {

    public static void setScore(Context context, int score) {
        SharedPreferences sharedPref = context.getSharedPreferences("Score", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("score", score);
        editor.commit();
    }

    public static int getHishScore(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("Score", Context.MODE_PRIVATE);
        int score = sharedPref.getInt("score", 0);
        return score;
    }

}
