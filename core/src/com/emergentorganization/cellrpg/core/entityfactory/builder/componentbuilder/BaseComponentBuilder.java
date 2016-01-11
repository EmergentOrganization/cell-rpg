package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.Name;

/**
 * Created by Brian on 1/11/2016.
 */
public abstract class BaseComponentBuilder implements IComponentBuilder {
    private final Aspect.Builder aspectBuilder;
    private final int sortImportance;
    private boolean isBuilt = false;

    public BaseComponentBuilder(Aspect.Builder aspectBuilder, int importance) {
        this.aspectBuilder = aspectBuilder;
        this.sortImportance = Math.max(Math.min(0, importance), 100);
    }

    /**
     * EntityBuilder class this method on the newly created entity
     * @param entity Entity to build upon
     */
    @Override
    public void build(Entity entity) {
        if (!aspectBuilder.build(entity.getWorld()).isInterested(entity)) {
            throw new RuntimeException("ERROR: " + entity.getComponent(Name.class).friendlyName + " does not have all required components.\n" +
                    "Refer to the aspect builder in the constructor of the culprit builder to see what configuration is required");
            // TODO: List required components somehow
        }
        isBuilt = true;
    }

    @Override
    public boolean isBuilt() {
        return isBuilt;
    }

    @Override
    public int getSortIndex() {
        return sortImportance;
    }
}
