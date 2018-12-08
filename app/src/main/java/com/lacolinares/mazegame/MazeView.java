package com.lacolinares.mazegame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.lacolinares.dynatime.DynaTime;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Colinares on 12/8/2018.
 */

public class MazeView extends View {

    private Context context;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Cell[][] cells;
    private Cell player, exit;
    private static final int COLS = 6, ROWS = 10;
    private static final float WALL_THICKNESS = 4;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint, exitPaint;
    private Random random;

    private TextView txtScore;
    private TextView txtTimer;
    private int SCORE = 0;

    private final int DURATION = 20000;
    private final int INTERVAL = 1000;

    private DynaTime dynaTime = null;

    public MazeView(Context context, @Nullable AttributeSet attrs, TextView txtScore, TextView txtTimer) {
        super(context, attrs);

        this.context = context;
        this.txtScore = txtScore;
        this.txtTimer = txtTimer;

        wallPaint = new Paint();
        wallPaint.setColor(getResources().getColor(R.color.colorWhite));
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        playerPaint = new Paint();
        playerPaint.setColor(getResources().getColor(R.color.colorDirtyWhite));

        exitPaint = new Paint();
        exitPaint.setColor(getResources().getColor(R.color.colorAccent));

        random = new Random();
        initTimer();
        setTimer();
        createMaze();
    }

    private void initTimer() {
        dynaTime = new DynaTime() {
            @Override
            public void onTimerStart(long millisUntilFinished) {
                String min = (millisUntilFinished % 60000 / 1000) != 0 ? (millisUntilFinished % 60000 / 1000) + "" : (millisUntilFinished % 60000 / 1000) + "0";
                String time = (millisUntilFinished / 60000) + ":" + min;
                txtTimer.setText("Time: " + time);
            }

            @Override
            public void onFinish() {
                if (checkExit()) {
                    SCORE += 10;
                    txtScore.setText("Score: " + SCORE);
                    cancelTimer();
                    createMaze();
                    initTimer();
                    setTimer();
                } else {
                    txtScore.setText("Score: " + SCORE);
                    cancelTimer();
                    /*createMaze();
                    initTimer();
                    setTimer();*/
                    showMessage();
                }
                invalidate();
            }
        };
    }

    private void showMessage() {
        SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sDialog.setTitleText("Game Over!");
        sDialog.setContentText("Your total score is : " + SCORE);
        sDialog.setConfirmText("OK");
        sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                MainGame mainGame = new MainGame();
                mainGame.exit();
            }
        });
    }

    private void setTimer() {
        dynaTime.setTime(DURATION);
        dynaTime.setInterval(INTERVAL);
        dynaTime.startTimer();
    }

    public void pauseTimer() {
        if (dynaTime.isRunning()) dynaTime.pauseTimer();
    }

    public void resumeTimer() {
        if (dynaTime.isPaused()) dynaTime.resumeTimer();
    }

    private void cancelTimer() {
        if (dynaTime.isRunning()) dynaTime.stopTimer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));

        int w = getWidth();
        int h = getHeight();

        if (w / h < COLS / ROWS) cellSize = w / (COLS + 1);
        else cellSize = h / (ROWS + 1);

        hMargin = (w - COLS * cellSize) / 2;
        vMargin = (h - ROWS * cellSize) / 2;

        canvas.translate(hMargin, vMargin);

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (cells[x][y].topWall)
                    canvas.drawLine(
                            x * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            y * cellSize,
                            wallPaint
                    );
                if (cells[x][y].leftWall)
                    canvas.drawLine(
                            x * cellSize,
                            y * cellSize,
                            x * cellSize,
                            (y + 1) * cellSize,
                            wallPaint
                    );
                if (cells[x][y].bottomWall)
                    canvas.drawLine(
                            x * cellSize,
                            (y + 1) * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint
                    );
                if (cells[x][y].rightWall)
                    canvas.drawLine(
                            (x + 1) * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint
                    );
            }
        }

        float margin = cellSize / 10;

        canvas.drawRect(
                player.col * cellSize + margin,
                player.row * cellSize + margin,
                (player.col + 1) * cellSize - margin,
                (player.row + 1) * cellSize - margin,
                playerPaint
        );
        canvas.drawRect(
                exit.col * cellSize + margin,
                exit.row * cellSize + margin,
                (exit.col + 1) * cellSize - margin,
                (exit.row + 1) * cellSize - margin,
                exitPaint
        );
    }

    private void movePlayer(Direction direction) {
        switch (direction) {
            case UP:
                if (!player.topWall)
                    player = cells[player.col][player.row - 1];
                break;
            case DOWN:
                if (!player.bottomWall)
                    player = cells[player.col][player.row + 1];
                break;
            case LEFT:
                if (!player.leftWall)
                    player = cells[player.col - 1][player.row];
                break;
            case RIGHT:
                if (!player.rightWall)
                    player = cells[player.col + 1][player.row];
                break;
        }
        if (checkExit()) {
            SCORE += 10;
            txtScore.setText("Score: " + SCORE);
            cancelTimer();
            createMaze();
            initTimer();
            setTimer();
        }
        invalidate();
    }

    public int getCurrentScore() {
        return SCORE;
    }

    private boolean checkExit() {
        /*if (player == exit) {
            SCORE += 10;
            txtScore.setText("Score: " + SCORE);
            cancelTimer();
            createMaze();
            initTimer();
            setTimer();
        }*/
        return player == exit;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();

            float playerCenterX = hMargin + (player.col + 0.5f) * cellSize;
            float playerCenterY = vMargin + (player.row + 0.5f) * cellSize;

            float dx = x - playerCenterX;
            float dy = y - playerCenterY;

            float absDx = Math.abs(dx);
            float absDy = Math.abs(dy);

            if (absDx > cellSize || absDy > cellSize) {
                if (absDx > absDy) {
                    //move in x-direction
                    if (dx > 0)
                        //move to the right
                        movePlayer(Direction.RIGHT);
                    else
                        //move to the left
                        movePlayer(Direction.LEFT);
                } else {
                    //move in y-direction
                    if (dy > 0)
                        //move down
                        movePlayer(Direction.DOWN);
                    else
                        //move up
                        movePlayer(Direction.UP);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void createMaze() {

        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        cells = new Cell[COLS][ROWS];

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        player = cells[0][0];
        exit = cells[COLS - 1][ROWS - 1];

        current = cells[0][0];
        current.visited = true;
        do {
            next = getNeighbor(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            } else {
                current = stack.pop();
            }
        } while (!stack.isEmpty());

    }

    private Cell getNeighbor(Cell cell) {
        ArrayList<Cell> neighbor = new ArrayList<>();

        //left neighbor
        if (cell.col > 0) {
            if (!cells[cell.col - 1][cell.row].visited) {
                neighbor.add(cells[cell.col - 1][cell.row]);
            }
        }
        //right neighbor
        if (cell.col < COLS - 1) {
            if (!cells[cell.col + 1][cell.row].visited) {
                neighbor.add(cells[cell.col + 1][cell.row]);
            }
        }
        //top neighbor
        if (cell.row > 0) {
            if (!cells[cell.col][cell.row - 1].visited) {
                neighbor.add(cells[cell.col][cell.row - 1]);
            }
        }
        //bottom neighbor
        if (cell.row < ROWS - 1) {
            if (!cells[cell.col][cell.row + 1].visited) {
                neighbor.add(cells[cell.col][cell.row + 1]);
            }
        }
        if (neighbor.size() > 0) {
            int index = random.nextInt(neighbor.size());
            return neighbor.get(index);
        }
        return null;
    }

    private void removeWall(Cell current, Cell next) {
        if (current.col == next.col && current.row == next.row + 1) {
            current.topWall = false;
            next.bottomWall = false;
        }
        if (current.col == next.col && current.row == next.row - 1) {
            current.bottomWall = false;
            next.topWall = false;
        }
        if (current.col == next.col + 1 && current.row == next.row) {
            current.leftWall = false;
            next.rightWall = false;
        }
        if (current.col == next.col - 1 && current.row == next.row) {
            current.rightWall = false;
            next.leftWall = false;
        }
    }

    private class Cell {
        boolean
                topWall = true,
                leftWall = true,
                bottomWall = true,
                rightWall = true,
                visited = false;
        int col, row;

        public Cell(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }
}
