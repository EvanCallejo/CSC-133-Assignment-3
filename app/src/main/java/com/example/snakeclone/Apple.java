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
        this.gameObjectRange = range;
        this.gameObjectSize = gameObjectSize;
        gameObject.x = -10;

        loadGameImageToBitmap(context);
    }

    // Let SnakeGame know where apple is and shares info with snake
    Point getAppleObject(){
        return this.gameObject;
    }

    public void draw(Canvas canvas, Paint paint)
    {
        canvas.drawBitmap(gameObjectBitmap,
                gameObject.x * gameObjectSize, gameObject.y * gameObjectSize, paint);

    }
}