package com.emergentorganization.cellrpg.scenes.game.regions;

import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion1 extends Region {
    public ArcadeRegion1(){
        super();
    }

    @Override
    public CALayer[] getCALayers(){
        return new CALayer[]{
                CALayer.ENERGY,
                CALayer.VYROIDS,
                CALayer.VYROIDS_MINI,
                CALayer.VYROIDS_MEGA
        };
    }
}
