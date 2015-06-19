package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EntityListNode {
    public String name;
    public Class<? extends Entity> entity;

    public EntityListNode(String name, Class<? extends Entity> entity) {
        this.name = name;
        this.entity = entity;
    }

    @Override
    public String toString() {
        return name;
    }
}
