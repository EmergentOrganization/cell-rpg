package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.World;
import com.emergentorganization.cellrpg.scenes.game.WorldScene;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Base class used to create an area (aka region chunk) within a scene.
 * Useful to allow switching between "region" units without breaking continuity of the scene,
 * such as moving to next arcade level or loading next area chunk while traveling.
 *
 * Created by 7yl4r on 10/10/2015,
 * updated to artemis build 2015-12-29 by 7yl4r.
 */
public interface iRegion {
    void loadRegion(World world);
    /* loads the region in preparation for insertion into the given world */

    void enterRegion(World world);
    /* enters the region, applying loaded region config to the world. */

    iRegion getNextRegion(World world);
    /* should return region ready to be switched to, else returns null
    * TODO: should this return the region class instead and leave instantiation to the regionSwitcher?
    */

    CALayer[] getCALayers();
    /* returns a list of CA layers present in this region */
}
