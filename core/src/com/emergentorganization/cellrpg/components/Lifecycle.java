package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Required for all entities.
 * Component which contains variables that define how the entity is constructed/deconstructed
 * as the player travels around the world.
 */
public class Lifecycle extends Component {
    // maximum distance from player before being deconstructed (can be set to -1 for infinite distance)
    public float maxPlayerDist = -1f;
}
