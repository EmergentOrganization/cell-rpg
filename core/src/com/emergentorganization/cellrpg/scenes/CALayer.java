package com.emergentorganization.cellrpg.scenes;

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

    public static CALayer[] vyroid_values(){
        // returns array of values only including those which are vyroids
        return new CALayer[]{
                VYROIDS_MINI,
                VYROIDS,
                VYROIDS_GENETIC,
                VYROIDS_MEGA
        };
    }
}
