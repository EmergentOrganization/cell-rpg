package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.util.CoordinateRecorder;

/**
 * Created by BrianErikson on 6/8/2015.
 */
public class PlayerUserData extends CellUserData {
    public final CoordinateRecorder cr;
    public PlayerUserData(MovementComponent movementComponent, CoordinateRecorder cr) {
        super(movementComponent, Tag.PLAYER);
        this.cr = cr;
    }
}
