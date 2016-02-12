package io.github.emergentorganization.cellrpg.scenes.game.regions;

import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * static helpers for building regions.
 */
public class RegionBuildTool {
    public static ArrayList<iRegion> pastRegions = new ArrayList<iRegion>();  // a list of the last few regions which have called getNextRegion
    public static iRegion getNextRegion(iRegion currentRegion){
        // used to get a new region
        pastRegions.add(currentRegion);


        // TODO: decide what to do based on region type:


        SingleShapeWarpRegion region = (SingleShapeWarpRegion) pastRegions.get(pastRegions.size()-1);
        long expireTime = region.maxLength-1000;
        if (expireTime < 10000) expireTime = 10000;  // minimum region time

        int newFreq = region.spawnFreq - 1;
        if (newFreq < 1) newFreq = 1;

        return new SingleShapeWarpRegion(region.scene, expireTime, getNextWarpShape(), newFreq, CALayer.vyroid_values());
    }

    private static int[][] getNextWarpShape(){
        SingleShapeWarpRegion region = (SingleShapeWarpRegion) pastRegions.get(pastRegions.size()-1);
        if (Arrays.deepEquals(region.shape, CGoLShapeConsts.BLINKER_H)){ // TODO: BLINKER_V
            return CGoLShapeConsts.BLOCK;
        } else if (Arrays.deepEquals(region.shape, CGoLShapeConsts.BLOCK)){
            return CGoLShapeConsts.GLIDER_DOWN_LEFT;
        } else if (Arrays.deepEquals(region.shape, CGoLShapeConsts.GLIDER_DOWN_LEFT)){ // TODO: other gliders too
            return CGoLShapeConsts.R_PENTOMINO;
        } else {
            return CGoLShapeConsts.BLINKER_H;
        }
    }
}
