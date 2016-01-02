package com.emergentorganization.cellrpg.systems.CASystems.layers;

import java.util.EnumMap;

/**
 * Created by 7yl4r on 8/9/2015.
 */
public enum CALayer {
    VYROIDS_MINI,
    VYROIDS,
    VYROIDS_MEGA,
    VYROIDS_GENETIC,
    SCENERY_COLLIDABLE,
    SCENERY_NONCOLLIDABLE,
    ENERGY;

    private static final EnumMap<CALayer, String> map = new EnumMap<CALayer, String>(CALayer.class);
    private static final String pre = "ca-layer-";
    static {
        map.put(VYROIDS,         pre + "vyroids");
        map.put(ENERGY,          pre + "energy" );
        map.put(VYROIDS_GENETIC, pre + "genetic");
    }

    public static CALayer[] vyroid_values(){
        // returns array of values only including those which are vyroids
        return new CALayer[]{
                VYROIDS_MINI,
                VYROIDS,
                VYROIDS_GENETIC,
                VYROIDS_MEGA
        };
    }

    @Override
    public String toString(){
        return map.get(this);
    }
}
