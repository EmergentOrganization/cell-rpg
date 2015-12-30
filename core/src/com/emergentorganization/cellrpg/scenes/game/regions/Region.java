package com.emergentorganization.cellrpg.scenes.game.regions;

import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Base class used to create an area (aka region chunk) within a scene.
 * Useful to allow switching between "region" units without breaking continuity of the scene,
 * such as moving to next arcade level or loading next area chunk while traveling.
 *
 * Created by 7yl4r on 10/10/2015,
 * updated to artemis build 2015-12-29 by 7yl4r.
 */
public abstract class Region {

    public Region(){

    }

    public abstract CALayer[] getCALayers();
    /* returns a list of CA layers present in this region */
}
