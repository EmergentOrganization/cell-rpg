package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.components.MovementComponent;

/**
 * Created by BrianErikson on 6/8/2015.
 */
public class CellUserData {
    public final Tag tag;
    public final MovementComponent movementComponent;

    public CellUserData(MovementComponent movementComponent, Tag tag) {
        this.movementComponent = movementComponent;
        this.tag = tag;
    }
}
