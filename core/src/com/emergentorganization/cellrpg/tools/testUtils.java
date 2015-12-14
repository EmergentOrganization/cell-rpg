package com.emergentorganization.cellrpg.tools;

import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Created by 7yl4r on 12/14/2015.
 */
public class testUtils {

    public static boolean ifStatesMatchAt(CAGridComponents testComps, int[][] expected, int row, int col){
        for (int i = 0; i < expected.length; i ++){
            int x = row + i;
            for (int j = 0; j < expected[0].length; j++){
                int y = col + j;
                if (testComps.getState(x,y) != expected[i][j]){
                    return false;
                }
            }
        }
        return true;
    }
}
