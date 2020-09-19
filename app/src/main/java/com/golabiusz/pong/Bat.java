package com.golabiusz.pong;

import android.graphics.RectF;

class Bat
{
    private RectF rect;
    private float length;
    private float height;
    private float speed;

    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    private int movementState = STOPPED;

    Bat(int screenWidth, int screenHeight)
    {
        length = screenWidth / 8;
        height = screenHeight / 40;

        rect = new RectF();
    }

    public RectF getRect()
    {
        return rect;
    }

    public void reset(int screenWidth, int screenHeight)
    {
        // Configure the starting location of the bat - the middle horizontally, the bottom vertically
        rect.left = screenWidth / 2 - length / 2;
        rect.top = screenHeight - height;
        rect.right = rect.left + length;
        rect.bottom = screenHeight;

        // Configure the speed of the bat - can cover the width of the screen in 1 second
        speed = screenWidth;
    }

    public void setMovementState(int state)
    {
        movementState = state;
    }

    public void updatePosition(long fps, int screenWidth)
    {
        float xCoord = rect.left;

        if (movementState == LEFT) {
            xCoord = xCoord - speed / fps;
        }

        if (movementState == RIGHT) {
            xCoord = xCoord + speed / fps;
        }

        if (xCoord < 0) {
            xCoord = 0;
        } else if (xCoord + length > screenWidth) {
            xCoord = screenWidth - length;
        }

        rect.left = xCoord;
        rect.right = xCoord + length;
    }
}
