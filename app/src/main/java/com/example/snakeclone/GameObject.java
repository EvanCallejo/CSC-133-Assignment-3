package com.example.snakeclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.BitmapFactory;
import java.util.Random;

public abstract class GameObject
{
    protected Bitmap gameObjectBitmap;
    protected int gameObjectSize;
    protected Point gameObjectRange;
    protected Point gameObject = new Point();
    public abstract void setUpGameObject(Context context, Point range, int gameObjectSize);
    public void reSpawnGameObject()
    {
        Random random = new Random();
        gameObject.x = random.nextInt(gameObjectRange.x) + 1;
        gameObject.y = random.nextInt(gameObjectRange.y - 1) + 1;
    }
    protected void loadGameImageToBitmap(Context context)
    {
        // Load the image to the bitmap
        gameObjectBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        gameObjectBitmap = Bitmap.createScaledBitmap(gameObjectBitmap, gameObjectSize, gameObjectSize, false);
    }
}
