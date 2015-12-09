package com.emergentorganization.cellrpg.scenes;

/**
 * Created by 7yl4r on 9/6/2015.
 */
public interface arcadeScore {
    int getScore();

    void addPoints(final int amount);

    void resetScore();
}
