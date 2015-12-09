package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by BrianErikson on 6/8/2015.
 */
public class PlayerUserData extends CellUserData {
    public final PlayerInputComponent playerInputComponent;
    public PlayerUserData(Entity entity, PlayerInputComponent pic) {
        super(entity, Tag.PLAYER);
        this.playerInputComponent = pic;
    }
}
