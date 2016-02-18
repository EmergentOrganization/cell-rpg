package io.github.emergentorganization.cellrpg.scenes.game.regions;

import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * static helpers for building regions.
 */
public class RegionBuildTool {
    public static ArrayList<iRegion> pastRegions = new ArrayList<iRegion>();  // a list of the last few regions which have called getNextRegion

    private static final Logger logger = LogManager.getLogger(RegionBuildTool.class);

    public static iRegion getNextRegion(iRegion currentRegion){
        // used to get a new region
        pastRegions.add(currentRegion);

        long expireTime = 1000;
        float newFreq = 1;

        if (currentRegion instanceof TimedRegion){
            TimedRegion region = (TimedRegion) pastRegions.get(pastRegions.size() - 1);
            expireTime = region.maxLength - 1000;
            if (expireTime < 10000) expireTime = 10000;  // minimum region time
        }


        // decide what to do based on region type:
        if (currentRegion instanceof SingleEntityWarpRegion) {
            SingleEntityWarpRegion region = (SingleEntityWarpRegion) pastRegions.get(pastRegions.size() - 1);

            // TODO: getNextEntity
            return new SingleEntityWarpRegion(region.scene, expireTime, newFreq, EntityID.VYRAPUFFER);

        } else if (currentRegion instanceof SingleShapeWarpRegion){
            SingleShapeWarpRegion region = (SingleShapeWarpRegion) pastRegions.get(pastRegions.size() - 1);

            if (Arrays.deepEquals(region.shape, CGoLShapeConsts.R_PENTOMINO)){  // if last shape in list
                // switch to SingleEntityWarpRegion

                //TODO:
                return new SingleEntityWarpRegion(region.scene, expireTime, newFreq, EntityID.TUBSNAKE);
            } else {


                return new SingleShapeWarpRegion(region.scene, expireTime, getNextWarpShape(), newFreq, CALayer.vyroid_values());
            }
        } else {
            logger.fatal("ERR: previous region type not recognized!");
            return null;
        }
    }

    private static int[][] getNextWarpShape(){
        SingleShapeWarpRegion region = (SingleShapeWarpRegion) pastRegions.get(pastRegions.size()-1);
        // TODO: need to iterate backwards through pastRegions to find last SingleShapeWarpRegion

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
