package com.emergentorganization.cellrpg.managers.RegionManager;

import com.emergentorganization.cellrpg.scenes.game.regions.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Leveled region switcher can be used at the Scene level to control switching of regions.
 * Scene initializes the switcher with region sequence to be used,
 * and triggers region switches by calling nextRegion().
 * Switcher will handle loading & prep of new region,
 * move player into the region and dispose of previous region.
 * Intended to enable easy creation of level-based gameflow.
 * Switcher also (optionally) displays level #.
 *
 * Adapted from LeveledRegionSwitcher (2015-10-12) by 7yl4r on 12/29/2015.
 */
public class LeveledRegionSwitcher extends iRegionManager {
    private final Logger logger = LogManager.getLogger(getClass());

    private List<Class> regions;
    private int currentRegionIndex = 0;
    private Region currentRegion;
    private Region nextRegion;

    public LeveledRegionSwitcher(List<Class> _regions){
        regions = _regions;
        loadRegion(0);
        nextRegion();
    }

    @Override
    public void begin(){

    }

    @Override
    public void processSystem(){
        // TODO: check if should move to next region
    }

    private boolean nextRegion(){
        // transition to next region in list.
        // returns true if next region is the final region
        currentRegionIndex += 1;
        enterNextRegion();
        loadRegion(currentRegionIndex);
        if (noMoreRegions()){
            return true;
        } else {
            return false;
        }
    }

    private int getRemainingRegions(){
        // returns the number of regions remaining, excluding current region
        return regions.size() - (currentRegionIndex + 1);
    }

    private boolean noMoreRegions(){
        // returns true if this is the last region
        return getRemainingRegions() == 0;
    }

    private void loadRegion(int index){
        // loads given region into nextRegion
        if (noMoreRegions()) {
            try {
                nextRegion = (Region) regions.get(index).newInstance();
            } catch (IllegalAccessException ex) {
                logger.error("region-list badness in regionList:" + regions + "\n\nERR: " + ex.getMessage());
                // TODO: return to main menu or something?
            } catch (InstantiationException ex){
                logger.error("region-list badness in regionList:" + regions + "\n\nERR: " + ex.getMessage());
                // TODO: return to main menu or something?
            }
        } else {
            logger.warn("cannot load next region; current region is last");
        }
    }

    private void enterNextRegion(){
        // makes nextRegion currentRegion and disposes of old region
        if (nextRegion == null){
            throw new IllegalStateException("cannot enter next region, nextRegion==null");
        }
        // TODO: currentRegion.dispose() ???
        currentRegion = nextRegion;
        // TODO:  nextRegion.init() ???
    }
}
