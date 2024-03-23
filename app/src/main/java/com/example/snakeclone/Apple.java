package com.example.snakeclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Apple extends GameObject implements IGameFeatures
{
    Apple(Context context, Point range, int gameObjectSize)
    {
        setUpGameObject(context, range, gameObjectSize);
    }
    public @Override void setUpGameObject(Context context, Point range, int gameObjectSize)
    {
        // Make a note of the passed in spawn range
        this.gameObjectRange = range;
        // Make a note of the size of an apple
        this.gameObjectSize = gameObjectSize;
        // Hide the apple off-screen until the game starts
        gameObject.x = -10;

        loadGameImageToBitmap(context);
    }

    // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getAppleObject(){
        return this.gameObject;
    }

    // Draw the apple
    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(gameObjectBitmap,
                gameObject.x * gameObjectSize, gameObject.y * gameObjectSize, paint);

    }

}