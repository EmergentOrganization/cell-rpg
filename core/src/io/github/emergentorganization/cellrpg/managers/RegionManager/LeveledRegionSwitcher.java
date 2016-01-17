package io.github.emergentorganization.cellrpg.managers.RegionManager;

import io.github.emergentorganization.cellrpg.scenes.game.regions.iRegion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Leveled region switcher can be used at the Scene level to control switching of regions.
 * Scene initializes the switcher with region sequence to be used,
 * and triggers region switches by calling nextRegion().
 * Switcher will handle loading & prep of new region,
 * move player into the region and dispose of previous region.
 * Intended to enable easy creation of level-based gameflow.
 * Switcher also (optionally) displays level #.
 * <p/>
 * Adapted from LeveledRegionSwitcher (2015-10-12) by 7yl4r on 12/29/2015.
 */
public class LeveledRegionSwitcher extends iRegionManager {
    private final Logger logger = LogManager.getLogger(getClass());

    private iRegion currentRegion;
    private iRegion nextRegion;

    public LeveledRegionSwitcher(iRegion startingRegion) {
        nextRegion = startingRegion;
    }

    @Override
    public void processSystem() {
        // check if should move to next region
        if (nextRegion != null) {
            nextRegion.loadRegion(world);  // or instantiate region here?

//            currentRegion.leave();
//            currentRegion.destroy();
            currentRegion = nextRegion;
            nextRegion = null;
            currentRegion.enterRegion(world);

        } else {
            nextRegion = currentRegion.getNextRegion(world);
        }
    }
}
