package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.World;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Base class used to create an area (aka region chunk) within a scene.
 * Useful to allow switching between "region" units without breaking continuity of the scene,
 * such as moving to next arcade level or loading next area chunk while traveling.
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
}
