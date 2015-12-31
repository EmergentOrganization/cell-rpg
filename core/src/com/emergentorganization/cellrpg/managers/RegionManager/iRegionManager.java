package com.emergentorganization.cellrpg.managers.RegionManager;

import com.artemis.BaseSystem;

/**
 * Interface to define the behavior of a manager for switching between regions within a world.
 *
 * Created by 7yl4r on 12/29/2015.
 */
public abstract class iRegionManager extends BaseSystem{

    @Override
    public void initialize() {
        super.initialize();
        // set up the first region before system processing begins
    }

    @Override
    public void processSystem(){
        // update the region based on position, time, or whatever.
    }

}
