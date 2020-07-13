package com.golabiusz.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceView;

class PongGame extends SurfaceView implements Runnable
{
    // Are we debugging?
    private final boolean DEBUGGING = true;
    // Starting number of lives
    private final int DEFAULT_LIVES = 3;
    // The number of milliseconds in a second
    private final int MILLIS_IN_SECOND = 1000;

    // These objects are needed to do the drawing
    private Canvas canvas;
    private Paint paint;

    // How many frames per second did we get?
    private long fps;

    private int screenWidth;
    private int screenHeight;
    private int fontSize;
    private int fontMargin;

    // The game objects
    private Bat bat;
    private Ball ball;

    // The current score and lives remaining
    private int score;
    private int lives;

    private Thread gameThread = null;
    private volatile boolean isPlaying;
    private boolean isPaused = true;

    public PongGame(Context context, int x, int y)
    {
        super(context);

        screenWidth = x;
        screenHeight = y;

        // Font is 5% (1/20th) of screen width
        fontSize = screenWidth / 20;
        // Margin is 1.5% (1/75th) of screen width
        fontMargin = screenWidth / 75;

        paint = new Paint();
        bat = new Bat();
        ball = new Ball();

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
                update();
                // Now the bat and ball are in their new positions
                // we can see if there have been any collisions
                detectCollisions();
            }

            // The movement has been handled and collisions detected
            // now we can draw the scene.
            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame > 0) {
                fps = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    private void update()
    {
        // Update the bat and the ball
    }

    private void detectCollisions()
    {
        // Has the bat hit the ball?

        // Has the ball hit the edge of the screen

        // Bottom

        // Top

        // Left

        // Right
    }

    private void startNewGame()
    {
        score = 0;
        lives = DEFAULT_LIVES;
    }

    // Draw the game objects and the HUD
    private void draw()
    {
        if (getHolder().getSurface().isValid()) {
            // Lock the canvas (graphics memory) ready to draw
            canvas = getHolder().lockCanvas();

            // Fill the screen with a solid color
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose a color to paint with
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the bat and ball

            // Choose the font size
            paint.setTextSize(fontSize);

            // Draw the HUD
            canvas.drawText("Score: " + score + "   Lives: " + lives, fontMargin, fontSize, paint);

            if (DEBUGGING) {
                printDebuggingText();
            }

            // Display the drawing on screen
            // unlockCanvasAndPost is a method of SurfaceView
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void printDebuggingText()
    {
        int debugSize = fontSize / 2;
        int debugStart = 150;

        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + fps, 10, debugStart + debugSize, paint);
    }
}
