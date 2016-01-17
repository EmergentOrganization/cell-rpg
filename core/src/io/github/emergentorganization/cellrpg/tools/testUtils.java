package io.github.emergentorganization.cellrpg.tools;

import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


public class testUtils {
    private static final Logger logger = LogManager.getLogger(testUtils.class);

    public static boolean ifStatesMatchAt(CAGridComponents testComps, int[][] expected, int row, int col) {
        logger.trace("checking state match:\n"
                + Arrays.deepToString(expected) // TODO: format print. maybe .replace("],[", "\n") ?
                + "\n =?= \n"
                + testComps.statesToString(row, col, expected.length, expected[0].length));
        for (int i = 0; i < expected.length; i++) {
            int x = row + i;
            for (int j = 0; j < expected[0].length; j++) {
                int y = col + j;
                logger.trace("(" + x + "," + y + ")" + testComps.getState(x, y) + "==" + expected[i][j] + "(" + i + "," + j + ")");
                if (testComps.getState(x, y) != expected[i][j]) {
                    logger.trace("FALSE");
                    return false;
                }
            }
        }
        logger.trace("TRUE");
        return true;
    }
}
