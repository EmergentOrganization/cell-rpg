package io.github.emergentorganization.cellrpg.core.entityfactory.Entities;

import com.artemis.Entity;

/**
 * Interface for an entity factory/creator which creates an entity.
 */
public abstract class EntityCreator {
    Entity ent;  // instance of entity which is being created

    public int getId() {
        // returns id of the entity which has been created
        return ent.getId();
    }
}
