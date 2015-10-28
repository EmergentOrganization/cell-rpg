package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by BrianErikson on 6/8/2015.
 */
public class CellUserData {
    public final Tag tag;
    public final Entity entity;

    public CellUserData(Entity entity, Tag tag) {
        this.entity = entity;
        this.tag = tag;
    }
}
