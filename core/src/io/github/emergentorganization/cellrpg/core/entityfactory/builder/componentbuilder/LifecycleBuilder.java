package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.core.components.Lifecycle;


public class LifecycleBuilder extends BaseComponentBuilder {
    private float maxDistanceFromPlayer;  // set to -1 for infinite distance

    public LifecycleBuilder(float maxDistanceFromPlayer) {
        super(Aspect.all(Lifecycle.class), 0);

        this.maxDistanceFromPlayer = maxDistanceFromPlayer;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        entity.getComponent(Lifecycle.class).maxPlayerDist = maxDistanceFromPlayer;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return Lifecycle.class;
    }
}
