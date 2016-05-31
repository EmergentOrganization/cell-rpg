package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;


public class CollectibleSpawnFieldBuilder extends BaseComponentBuilder {
    private float spawnFieldRadius;

    public CollectibleSpawnFieldBuilder(float spawnFieldRadius) {
        super(Aspect.all(EntitySpawnField.class), 0);
        this.spawnFieldRadius = spawnFieldRadius;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        EntitySpawnField spawnField = entity.getComponent(EntitySpawnField.class);
        spawnField.radius = spawnFieldRadius;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return EntitySpawnField.class;
    }
}
