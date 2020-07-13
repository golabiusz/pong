package com.golabiusz.pong;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class PongActivity extends AppCompatActivity
{
    private PongGame pongGame;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        pongGame = new PongGame(this, size.x, size.y);
        setContentView(pongGame);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        pongGame.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        pongGame.pause();
    }
}
