package io.github.emergentorganization.cellrpg.systems.CASystems.layers;

import io.github.emergentorganization.cellrpg.core.EntityID;

import java.util.EnumMap;


public enum CALayer {
    VYROIDS_MINI,
    VYROIDS,
    VYROIDS_MEGA,
    VYROIDS_GENETIC,
    //SCENERY_COLLIDABLE,
    //SCENERY_NONCOLLIDABLE,
    ENERGY;

    private static final EnumMap<CALayer, String> map = new EnumMap<CALayer, String>(CALayer.class);
    private static final String pre = "ca-layer-";

    static {
        map.put(VYROIDS, pre + "vyroids");
        map.put(ENERGY, pre + "energy");
        map.put(VYROIDS_GENETIC, pre + "genetic");
    }

    public static CALayer[] vyroid_values() {
        // returns array of values only including those which are vyroids
        return new CALayer[]{
                //VYROIDS_MINI,
                VYROIDS,
                VYROIDS_GENETIC,
                //VYROIDS_MEGA
        };
    }

    @Override
    public String toString() {
        return map.get(this);
    }

    public EntityID getId() {
        return EntityID.fromString(toString());
    }

    public String getTag() {
        return toString();
    }
}
