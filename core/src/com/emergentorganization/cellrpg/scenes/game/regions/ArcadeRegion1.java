package com.emergentorganization.cellrpg.scenes.game.regions;

import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion1 implements iRegion {
    public ArcadeRegion1(){
        super();
    }

    public CALayer[] getCALayers(){
        // TODO: this is currently unused, but layers should be dynamically added/removed
        // TODO:    by a CA Manager.
        return new CALayer[]{
                CALayer.ENERGY,
                CALayer.VYROIDS,
                CALayer.VYROIDS_MINI,
                CALayer.VYROIDS_MEGA
        };
    }

    public iRegion getNextRegion(){
        return null;
    }

//    public void loadRegion(){
//        setCAEdgeSpawns();
//    }
//
//    private void setCAEdgeSpawns(){
//        CAEdgeSpawnType.EMPTY;
//    }
}
