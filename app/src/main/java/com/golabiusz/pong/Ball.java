package com.golabiusz.pong;

import android.graphics.RectF;

class Ball
{
    private RectF rect;
    private float width;
    private float height;
    private float xVelocity;
    private float yVelocity;

    Ball(int screenWidth)
    {
        width = screenWidth / 100;
        height = screenWidth / 100;

        rect = new RectF();
    }

    public RectF getRect()
    {
        return rect;
    }

    public void reset(int screenWidth, int screenHeight)
    {
        int topOffset = 1;

        rect.left = screenWidth / 2;
        rect.top = topOffset;
        rect.right = rect.left + width;
        rect.bottom = height + topOffset;

        xVelocity = (screenHeight / 3);
        yVelocity = xVelocity;
    }

    public void updatePosition(long fps)
    {
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);

        rect.right = rect.left + width;
        rect.bottom = rect.top + height;
    }

    public void increaseVelocity()
    {
        // increase the speed by 10%
        xVelocity = xVelocity * 1.1f;
        yVelocity = yVelocity * 1.1f;
    }

    public void reverseYVelocity()
    {
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity()
    {
        xVelocity = -xVelocity;
    }

    public void batBounce(RectF batPosition)
    {
        float batCenter = batPosition.left + (batPosition.width() / 2);
        float ballCenter = rect.left + (width / 2);

        float relativeIntersect = (batCenter - ballCenter);
        if (relativeIntersect < 0) {
            goRight();
        } else {
            goLeft();
        }

        reverseYVelocity();
    }

    private void goRight()
    {
        xVelocity = Math.abs(xVelocity);
    }

    private void goLeft()
    {
        xVelocity = -Math.abs(xVelocity);
    }
}
