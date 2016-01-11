package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Entity;

/**
 * Created by Brian on 1/11/2016.
 */
public interface IComponentBuilder {
    void build(Entity entity);
    boolean isBuilt();
    int getSortIndex();
}
