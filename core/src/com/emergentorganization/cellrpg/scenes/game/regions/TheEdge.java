package com.emergentorganization.cellrpg.scenes.game.regions;

import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class TheEdge extends Region {

    public TheEdge(Scene parentScene){
        super();
    }

    @Override
    public CALayer[] getCALayers() {
        return new CALayer[]{
                CALayer.ENERGY,
                CALayer.VYROIDS
        };
    }
}
