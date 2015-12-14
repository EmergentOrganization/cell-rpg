package com.emergentorganization.cellrpg.tools;

/**
 * static helper class that holds useful conway's game of life shapes.
 *
 * Created by 7yl4r on 12/14/2015.
 */
public class CGoLShapeConsts {
    public static final int[][] BLINKER_H = {
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,1,1,1,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
    };
    public static final int[][] BLINKER_V = {
            {0,0,0,0,0,0,0},
            {0,0,0,1,0,0,0},
            {0,0,0,1,0,0,0},
            {0,0,0,1,0,0,0},
            {0,0,0,0,0,0,0},
    };

    public static final int[][] BLOCK = {
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,1,1,0,0},
            {0,0,1,1,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
    };
}
