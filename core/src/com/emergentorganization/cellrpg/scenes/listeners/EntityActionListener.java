package com.emergentorganization.cellrpg.scenes.listeners;

import com.emergentorganization.cellrpg.entities.Entity;

/**
 * Created by BrianErikson on 7/6/2015.
 */

/**
 * A listener that you can add to the scene to call code that deals with scene/entity interactions
 */
public abstract class EntityActionListener {
    private Class<? extends Entity> entityClass;

    public EntityActionListener(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    /**
     * Override this method in an extended class to call code when an entity is added to the scene
     */
    public void onAdd() {}

    public void onRemove() {}
}
