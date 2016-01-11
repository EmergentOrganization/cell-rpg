package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.CollectibleSpawnField;

/**
 * Created by Brian on 1/11/2016.
 */
public class CollectibleSpawnFieldBuilder extends BaseComponentBuilder {
    private float spawnFieldRadius;

    public CollectibleSpawnFieldBuilder(float spawnFieldRadius) {
        super(Aspect.all(CollectibleSpawnField.class), 0);
        this.spawnFieldRadius = spawnFieldRadius;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        CollectibleSpawnField spawnField = entity.getComponent(CollectibleSpawnField.class);
        spawnField.radius = spawnFieldRadius;
    }
}
