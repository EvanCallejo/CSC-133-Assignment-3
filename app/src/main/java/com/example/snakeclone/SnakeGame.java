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

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;

    private boolean mPausedClicked = false;

    private Context mContext;


    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size)
    {
        super(context);

        mContext = context;

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
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

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        }
        catch (IOException e)
        {
            System.out.println("Something went wrong");
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                blockSize);

    }


    // Called to start a new game
    public void newGame()
    {

        // reset the snake
        mSnake.reSpawnGameObject(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.reSpawnGameObject();

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }


    // Handles the game loop
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

            //draw();
            // Do all the drawing
            if (mSurfaceHolder.getSurface().isValid())
            {
                mCanvas = mSurfaceHolder.lockCanvas();

                //Sets text font
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "spiral_bitmap.ttf");
                mPaint.setTypeface(typeface);

                drawBackground();
                if(mPaused && !mPausedClicked)
                {
                    drawStartMenu();
                }
                else
                {
                    drawPauseButton();
                }

                // Unlock the mCanvas and reveal the graphics for this frame
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired()
    {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
    public void update()
    {

        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getAppleObject())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            mApple.reSpawnGameObject();

            // Add to  mScore
            mScore = mScore + 1;

            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (mSnake.detectDeath())
        {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            mPaused =true;
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

    // Do all the drawing
    /*public void draw()
    {
        //Sets text font
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "spiral_bitmap.ttf");
        mPaint.setTypeface(typeface);

        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid())
        {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            mCanvas.drawColor(Color.CYAN);

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);

            // Draw the score
            mCanvas.drawText("" + mScore, 20, 120, mPaint);

            // Draw the apple and the snake
            mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Draw some text while paused
            if(mPaused && !mPausedClicked)
            {

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(100);

                // Draw the message
                // We will give this an international upgrade soon
                //mCanvas.drawText("Tap To Play!", 200, 700, mPaint);
                mCanvas.drawText(getResources().
                                getString(R.string.tap_to_play),
                        100, 400, mPaint);

                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(25);
                mCanvas.drawText(getResources().
                                getString(R.string.display_names),
                        400, 75, mPaint);


            }
            else
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
                //mCanvas.drawText("Pause", 300, 1150, mPaint);
            }

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }*/

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


    // Stop the thread
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


    // Start the thread
    public void resume()
    {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
