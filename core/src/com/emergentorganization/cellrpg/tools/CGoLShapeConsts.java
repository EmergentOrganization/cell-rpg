package com.emergentorganization.cellrpg.tools;

import com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;

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

    public static final int[][] GLIDER_DOWN_RIGHT = {
            {0,0,0,0,0},
            {0,0,1,0,0},
            {0,0,0,1,0},
            {0,1,1,1,0},
            {0,0,0,0,0},
    };

    public static final int[][] GLIDER_UP_RIGHT = {
            {0,0,0,0,0},
            {0,0,1,1,0},
            {0,1,0,1,0},
            {0,0,0,1,0},
            {0,0,0,0,0},
    };

    public static final int[][] GLIDER_DOWN_LEFT = {
            {0,0,0,0,0},
            {0,1,0,0,0},
            {0,1,0,1,0},
            {0,1,1,0,0},
            {0,0,0,0,0},
    };

    public static final int[][] GLIDER_UP_LEFT = {
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,1,0,0,0},
            {0,0,1,0,0},
            {0,0,0,0,0},
    };

    public static int[][] SQUARE(final int w, final int h, final int fill_state){
        int[][] res = new int[w][h];
        for (int i = 0; i < res.length; i++){
            for (int j = 0; j < res[0].length; j++){
                res[i][j] = fill_state;
            }
        }
        return res;
    }
    public static int[][] EMPTY(final int w, final int h){
        // returns matrix of 0s with given width and height.
        return new int[w][h];
    }

    public static int[][] BOOM(final int w, final int h){
        // returns an "explosion" drawn using non-typical (assumed visual-only) states of given width and height

//        //TODO: return something like this instead:
//        return new int[][]{
//                {5, 5, 5, 5, 5, 5},
//                {5, 4, 4, 4, 4, 5},
//                {5, 4, 3, 3, 3, 5},
//                {5, 4, 3, 2, 3, 5},
//                {5, 4, 3, 3, 3, 5},
//                {5, 4, 4, 4, 4, 5},
//                {5, 5, 5, 5, 5, 5}
//        };

        return SQUARE(w, h, DecayCellRenderer.getMaxOfColorGroup(DecayCellRenderer.colorGroupKeys.FIRE));
    }
}
