package com.emergentorganization.cellrpg.scenes;

import com.emergentorganization.cellrpg.scenes.regions.GeneticRegion;
import com.emergentorganization.cellrpg.scenes.regions.Region;

/**
 * Copies gameplay of arcade version, but with only genetic CA.
 *
 * Created by 7yl4r on 2015-10-13.
 */
public class GeneticArcade extends ArcadeScene {
    @Override
    public Region getStartingRegion() {
        return new GeneticRegion(this);
    }

    @Override
    protected CALayer getGenInsertLayer() {
        return CALayer.VYROIDS_GENETIC;
    }
}
