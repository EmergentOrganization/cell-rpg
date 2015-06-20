package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.tools.CoordinateRecorder;

/**
 * Created by BrianErikson on 6/8/2015.
 */
public class PlayerUserData extends CellUserData {
    public final CoordinateRecorder cr;
    public PlayerUserData(Entity entity, CoordinateRecorder cr) {
        super(entity, Tag.PLAYER);
        this.cr = cr;
    }
}
