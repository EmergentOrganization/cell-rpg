package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by BrianErikson on 6/17/2015.
 */

/**
 * This class holds references for drawing node gizmos for manipulation of the selected entity in the map editor
 */
public class MapTarget {
    public Entity target;
    public Vector2 size;
    public MovementComponent movementComponent;

    public MapTarget(Entity target, Vector2 size, MovementComponent movementComponent) {
        this.target = target;
        this.size = size;
        this.movementComponent = movementComponent;
    }
}
