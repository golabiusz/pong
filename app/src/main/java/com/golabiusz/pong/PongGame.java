package com.golabiusz.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.io.IOException;

class PongGame extends SurfaceView implements Runnable
{
    private final boolean DEBUGGING = true;
    private final int DEFAULT_LIVES = 3;
    private final int MILLIS_IN_SECOND = 1000;

    private Canvas canvas;
    private Paint paint;

    private long fps;

    private int screenWidth;
    private int screenHeight;
    private int fontSize;
    private int fontMargin;

    private Ball ball;
    private Bat bat;

    private int score;
    private int bestScore;
    private int lives;

    private Thread gameThread = null;
    private volatile boolean isPlaying;
    private boolean isPaused = true;

    private SoundPool sp;
    private int beepID = -1;
    private int boopID = -1;
    private int bopID = -1;
    private int missID = -1;

    public PongGame(Context context, int screenWidth, int screenHeight)
    {
        super(context);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        fontSize = screenWidth / 20;
        fontMargin = screenWidth / 75;

        paint = new Paint();
        ball = new Ball(screenWidth);
        bat = new Bat(screenWidth, screenHeight);

        this.loadSounds(context);

        startNewGame();
    }

    // This method is called by PongActivity when the player quits the game
    public void pause()
    {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "stopping thread");
        }
    }

    // This method is called by PongActivity when the player starts the game
    public void resume()
    {
        isPlaying = true;

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run()
    {
        // isPlaying gives us finer control rather than just relying on the calls to run
        while (isPlaying) {
            long frameStartTime = System.currentTimeMillis();

            if (!isPaused) {
                updateObjectsPosition();
                detectCollisions();
            }

            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame > 0) {
                fps = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                isPaused = false;

                if (motionEvent.getX() > screenWidth / 2) {
                    bat.setMovementState(bat.RIGHT);
                } else {
                    bat.setMovementState(bat.LEFT);
                }

                break;

            // TODO: It is possible to create bugs by using multiple fingers.
            case MotionEvent.ACTION_UP:
                bat.setMovementState(bat.STOPPED);
                break;
        }

        return true;
    }

    private void updateObjectsPosition()
    {
        ball.updatePosition(fps);
        bat.updatePosition(fps, screenWidth);
    }

    private void detectCollisions()
    {
        if (RectF.intersects(bat.getRect(), ball.getRect())) {
            ball.batBounce(bat.getRect());
            ball.increaseVelocity();
            score++;
            sp.play(beepID, 1, 1, 0, 0, 1);
        }

        if (ball.getRect().bottom >= screenHeight) {
            ball.reverseYVelocity();

            lives--;
            sp.play(missID, 1, 1, 0, 0, 1);

            if (lives == 0) {
                isPaused = true;
                startNewGame();

                return;
            }
        } else if (ball.getRect().top <= 0) {
            ball.reverseYVelocity();
            sp.play(boopID, 1, 1, 0, 0, 1);
        }

        if (ball.getRect().left <= 0) {
            ball.reverseXVelocity();
            sp.play(bopID, 1, 1, 0, 0, 1);
        } else if (ball.getRect().right >= screenWidth) {
            ball.reverseXVelocity();
            sp.play(bopID, 1, 1, 0, 0, 1);
        }
    }

    private void loadSounds(Context context)
    {
        sp = new SoundPoolBuilder().build();

        // Open each of the sound files in turn and load them into RAM ready to play
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            beepID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("boop.ogg");
            boopID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("bop.ogg");
            bopID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("miss.ogg");
            missID = sp.load(descriptor, 0);
        } catch (IOException e) {
            Log.d("error", "failed to load sound files");
        }
    }

    private void startNewGame()
    {
        ball.reset(screenWidth, screenHeight);
        bat.reset(screenWidth, screenHeight);

        bestScore = Math.max(bestScore, score);
        score = 0;
        lives = DEFAULT_LIVES;
    }

    private void draw()
    {
        if (getHolder().getSurface().isValid()) {
            // Lock the canvas (graphics memory) ready to draw
            canvas = getHolder().lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawRect(ball.getRect(), paint);
            canvas.drawRect(bat.getRect(), paint);

            drawHUD();

            if (DEBUGGING) {
                printDebuggingText();
            }

            // Display the drawing on screen
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawHUD()
    {
        paint.setColor(Color.argb(255, 0, 255, 0));
        paint.setTextSize(fontSize);

        canvas.drawText(
            "Score: " + score + "   Lives: " + lives + "   Best score: " + bestScore,
            fontMargin,
            fontSize,
            paint
        );
    }

    private void printDebuggingText()
    {
        int debugSize = fontSize / 2;
        int debugStart = 150;

        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + fps, 10, debugStart + debugSize, paint);
    }
}
