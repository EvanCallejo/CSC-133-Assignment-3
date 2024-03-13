package com.example.snakeclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

class Apple
{
    private Point appleLocateNotPixel = new Point();
    private Point appleSpawnRange;
    private int appleSize;
    private Bitmap appleImageBitmap;

    Apple(Context context, Point appleSpawnRange, int appleSize)
    {

        // Make a note of the passed in spawn range
        this.appleSpawnRange = appleSpawnRange;
        // Make a note of the size of an apple
        this.appleSize = appleSize;
        // Hide the apple off-screen until the game starts
        appleLocateNotPixel.x = -10;

        // Load the image to the bitmap
        appleImageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        appleImageBitmap = Bitmap.createScaledBitmap(appleImageBitmap, appleSize, appleSize, false);
    }

    void spawnAppleWhenEaten()
    {
        Random random = new Random();
        appleLocateNotPixel.x = random.nextInt(appleSpawnRange.x) + 1;
        appleLocateNotPixel.y = random.nextInt(appleSpawnRange.y - 1) + 1;
    }

    // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getAppleLocateNotPixel(){
        return this.appleLocateNotPixel;
    }

    // Draw the apple
    void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(appleImageBitmap,
                appleLocateNotPixel.x * appleSize, appleLocateNotPixel.y * appleSize, paint);

    }

}