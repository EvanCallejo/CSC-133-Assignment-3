package com.example.snakeclone;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable
{
    private Thread mThread = null;
    private long mNextFrameTime;
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private final SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;
    private final int NUM_BLOCKS_WIDE = 40;
    private final int mNumBlocksHigh;
    private int mScore;
    private Canvas mCanvas;
    private final SurfaceHolder mSurfaceHolder;
    private final Paint mPaint;
    private final Snake mSnake;
    private final Apple mApple;

    private boolean mPausedClicked = false;

    private final Context mContext;

    public SnakeGame(Context context, Point size)
    {
        super(context);

        mContext = context;
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else
        {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try
        {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        }
        catch (IOException e)
        {
            System.out.println("Something went wrong");
        }
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);

    }

    public void newGame()
    {
        mSnake.reSpawnGameObject(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mApple.reSpawnGameObject();
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
    }

    @Override
    public void run()
    {
        while (mPlaying)
        {
            if(!mPaused)
            {
                // Update 10 times a second
                if (updateRequired())
                {
                    update();
                }
            }
            // Do all the drawing
            if (mSurfaceHolder.getSurface().isValid())
            {
                mCanvas = mSurfaceHolder.lockCanvas();

                //Sets text font
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "spiral_bitmap.ttf");
                mPaint.setTypeface(typeface);

                drawBackground();
                checkPaused();
                // Unlock the mCanvas and reveal the graphics for this frame
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void drawBackground()
    {
        // Fill the screen with a color
        mCanvas.drawColor(Color.CYAN);

        // Set the size and color of the mPaint for the text
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(120);

        // Draw the score
        mCanvas.drawText("" + mScore, 20, 120, mPaint);

        // Draw the apple and the snake
        mApple.draw(mCanvas, mPaint);
        mSnake.draw(mCanvas, mPaint);
    }

    private void drawStartMenu()
    {
        // Set the size and color of the mPaint for the text
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(100);

        // Draw the message
        mCanvas.drawText(getResources().
                        getString(R.string.tap_to_play),
                100, 400, mPaint);

        //Draw author names
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(25);
        mCanvas.drawText(getResources().
                        getString(R.string.display_names),
                400, 75, mPaint);
    }

    private void drawPauseButton()
    {
        //Draw button background
        mPaint.setColor(Color.BLACK);
        mCanvas.drawRect(250, 1085, 500, 1175, mPaint);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(50);

        if (mPausedClicked)
        {
            mCanvas.drawText("Resume", 300, 1150, mPaint);
        }
        else
        {
            mCanvas.drawText("Pause", 300, 1150, mPaint);
        }
    }

    private void checkPaused()
    {
        if(mPaused && !mPausedClicked)
        {
            drawStartMenu();
        }
        else
        {
            drawPauseButton();
        }
    }

    public boolean updateRequired()
    {
        final long TARGET_FPS = 10;
        final long MILLIS_PER_SECOND = 1000;

        if(mNextFrameTime <= System.currentTimeMillis())
        {
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;
            return true;
        }

        return false;
    }

    public void update()
    {
        mSnake.move();

        if(mSnake.checkDinner(mApple.getAppleObject())){
            mApple.reSpawnGameObject();
            mScore = mScore + 1;
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        if (mSnake.detectDeath())
        {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            mPaused = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_UP:
                if (mPaused)
                {
                    mPaused = false;
                    if (x >= 250 && x <= 500 && y >= 1085 && y <= 1175)
                    {
                        mPausedClicked = false;
                        // Let the Snake class handle the input
                        mSnake.switchHeading(motionEvent);
                    }
                    else
                    {
                        newGame();
                    }
                    newGame();

                    // Don't want to process snake direction for this tap
                    return true;
                }

                if (x >= 250 && x <= 500 && y >= 1085 && y <= 1175)
                {
                    mPaused = true;
                    mPausedClicked = true;
                }
                else
                {
                    // Let the Snake class handle the input
                    mSnake.switchHeading(motionEvent);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void pause()
    {
        mPlaying = false;
        try
        {
            mThread.join();
        }
        catch (InterruptedException e)
        {
            System.out.println("Something went wrong");
        }
    }

    public void resume()
    {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
