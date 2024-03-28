package com.example.snakeclone;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake extends GameObject implements ISnakeFeatures
{
    private ArrayList<Point> segmentLocations;

    private int halfWayPoint;

    private enum movementHeading
    {
        UP, RIGHT, DOWN, LEFT
    }

    private movementHeading heading = movementHeading.RIGHT;
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;
    private Bitmap mBitmapBody;

    Snake(Context context, Point mr, int gameObjectSize)
    {
        setUpGameObject(context, mr, gameObjectSize);
    }
    public @Override void setUpGameObject(Context context, Point range, int gameObjectSize)
    {
        segmentLocations = new ArrayList<>();
        this.gameObjectSize = gameObjectSize;
        this.gameObjectRange = range;
        loadGameImageToBitmap(context);
    }
    protected @Override void loadGameImageToBitmap(Context context)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        createBitMapHeadR(context);
        createBitMapHeadL(context, matrix);
        createBitMapHeadU(context, matrix);
        createBitMapHeadD(context, matrix);
        createBitMapBody(context);
        halfWayPoint = gameObjectRange.x * gameObjectSize / 2;
    }
    private void createBitMapHeadR(Context context){
        mBitmapHeadRight = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);

        mBitmapHeadRight = Bitmap
                .createScaledBitmap(mBitmapHeadRight,
                        gameObjectSize, gameObjectSize, false);
    }
    private void createBitMapHeadL(Context context, Matrix matrix){
        mBitmapHeadLeft = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);
        mBitmapHeadLeft = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, gameObjectSize, gameObjectSize, matrix, true);
    }
    private void createBitMapHeadU(Context context, Matrix matrix){
        mBitmapHeadUp = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);
        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, gameObjectSize, gameObjectSize, matrix, true);
    }
    private void createBitMapHeadD(Context context, Matrix matrix){
        mBitmapHeadDown = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.head);
        matrix.preRotate(180);
        mBitmapHeadDown = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, gameObjectSize, gameObjectSize, matrix, true);

    }
    private void createBitMapBody(Context context){
        mBitmapBody = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.body);

        mBitmapBody = Bitmap
                .createScaledBitmap(mBitmapBody,
                        gameObjectSize, gameObjectSize, false);
    }

    public void reSpawnGameObject(int w, int h)
    {
        heading = movementHeading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    public void move()
    {
        for (int i = segmentLocations.size() - 1; i > 0; i--)
        {
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }
        Point p = segmentLocations.get(0);

        switch (heading)
        {
            case UP:
                p.y--;
                break;

            case RIGHT:
                p.x++;
                break;

            case DOWN:
                p.y++;
                break;

            case LEFT:
                p.x--;
                break;
        }

    }

    public boolean detectDeath()
    {
        boolean dead = false;

        if (segmentLocations.get(0).x == -1 ||
                segmentLocations.get(0).x > gameObjectRange.x ||
                segmentLocations.get(0).y == -1 ||
                segmentLocations.get(0).y > gameObjectRange.y)
        {
            dead = true;
        }
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (segmentLocations.get(0).x == segmentLocations.get(i).x &&
                    segmentLocations.get(0).y == segmentLocations.get(i).y)
            {
                dead = true;
            }
        }
        return dead;
    }

    public boolean checkDinner(Point l)
    {
        if (segmentLocations.get(0).x == l.x &&
                segmentLocations.get(0).y == l.y)
        {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas, Paint paint)
    {
        if (!segmentLocations.isEmpty())
        {
            switch (heading)
            {
                case RIGHT:
                    canvas.drawBitmap(mBitmapHeadRight,
                            segmentLocations.get(0).x
                                    * gameObjectSize,
                            segmentLocations.get(0).y
                                    * gameObjectSize, paint);
                    break;

                case LEFT:
                    canvas.drawBitmap(mBitmapHeadLeft,
                            segmentLocations.get(0).x
                                    * gameObjectSize,
                            segmentLocations.get(0).y
                                    * gameObjectSize, paint);
                    break;

                case UP:
                    canvas.drawBitmap(mBitmapHeadUp,
                            segmentLocations.get(0).x
                                    * gameObjectSize,
                            segmentLocations.get(0).y
                                    * gameObjectSize, paint);
                    break;

                case DOWN:
                    canvas.drawBitmap(mBitmapHeadDown,
                            segmentLocations.get(0).x
                                    * gameObjectSize,
                            segmentLocations.get(0).y
                                    * gameObjectSize, paint);
                    break;
            }
            for (int i = 1; i < segmentLocations.size(); i++)
            {
                canvas.drawBitmap(mBitmapBody,
                        segmentLocations.get(i).x
                                * gameObjectSize,
                        segmentLocations.get(i).y
                                * gameObjectSize, paint);
            }
        }
    }

    public void switchHeading(MotionEvent motionEvent)
    {
        if (motionEvent.getX() >= halfWayPoint)
        {
            switch (heading)
            {
                // Rotate clockwise
                case UP:
                    heading = movementHeading.RIGHT;
                    break;
                case RIGHT:
                    heading = movementHeading.DOWN;
                    break;
                case DOWN:
                    heading = movementHeading.LEFT;
                    break;
                case LEFT:
                    heading = movementHeading.UP;
                    break;

            }
        }
        else
        {
            switch (heading)
            {
                case UP:
                    heading = movementHeading.LEFT;
                    break;
                case LEFT:
                    heading = movementHeading.DOWN;
                    break;
                case DOWN:
                    heading = movementHeading.RIGHT;
                    break;
                case RIGHT:
                    heading = movementHeading.UP;
                    break;
            }
        }
    }
}