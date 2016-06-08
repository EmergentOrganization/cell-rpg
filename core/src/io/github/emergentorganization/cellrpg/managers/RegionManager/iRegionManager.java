package io.github.emergentorganization.cellrpg.managers.RegionManager;

import com.artemis.BaseSystem;

/**
 * Interface to define the behavior of a manager for switching between regions within a world.
 */
abstract class iRegionManager extends BaseSystem {

    @Override
    public void initialize() {
        super.initialize();
        // set up the first region before system processing begins
    }

    @Override
    public void processSystem() {
        // update the region based on position, time, or whatever.
    }

}
