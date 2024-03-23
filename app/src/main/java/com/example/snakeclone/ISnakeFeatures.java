package com.example.snakeclone;

import android.graphics.Point;

public interface ISnakeFeatures extends IGameFeatures
{
    void move();
    boolean detectDeath();
    boolean checkDinner(Point l);
}
