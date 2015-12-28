package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Created by 7yl4r on 12/16/2015.
 */
public class util {
    public static int ca_rule(final int row, final int col, CAGridComponents gridComps){
        // TODO: check gridComps.CARule and execute appropriate rule
        return ca_rule_CGoL(row, col, gridComps);
    }

    public static int ca_rule_CGoL(final int row, final int col, CAGridComponents gridComps) {
        // computes the rule at given row, col in cellStates array, returns result
        // Conway's Game of Life:
        switch (getNeighborhoodSum(row, col, 3, gridComps)) {
            case 2:
                return gridComps.getState(row, col);
            case 3:
                return 1;
            default:
                return 0;
        }
        // random state:
        //return Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
    }
    public static int[][] getNeighborhood(final int row, final int col, final int size, CAGridComponents gridComps) {
        // returns matrix of neighborhood around (row, col) with edge size "size"
        // size MUST be odd! (not checked for efficiency)

        // checkSize(size);
        int radius = (size - 1) / 2;
        int[][] neighbors = new int[size][size];
        for (int i = row - radius; i <= row + radius; i++) {
            int neighbor_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int neighbor_j = j - col + radius;
                neighbors[neighbor_i][neighbor_j] = gridComps.getState(i, j);
            }
        }
        return neighbors;
    }

    private void checkNeighborhoodSize(int size) {
        if (size % 2 < 1) {
            throw new UnsupportedOperationException("size must be odd!");
        } else {
            return;
        }
    }

    public static int getNeighborhoodSum(final int row, final int col, final int size, CAGridComponents gridComps) {
        // returns sum of all states in neighborhood
        // size MUST be odd! (not checked for efficiency)
        final boolean SKIP_SELF = true;

        // checkNeighborhoodSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            for (int j = col - radius; j <= col + radius; j++) {
                if (SKIP_SELF && i - row + radius == j - col + radius && i - row + radius == radius) {
                    continue;
                } else {
                    sum += gridComps.getLastState(i, j);
                }
            }
        }
//        logger.info("count: " + sum);
        return sum;
    }
}
