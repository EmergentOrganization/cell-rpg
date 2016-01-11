package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.DestructionTimer;

/**
 * Created by Brian on 1/11/2016.
 */
public class DestructionTimerBuilder extends BaseComponentBuilder {
    private long timeToDestruction;

    public DestructionTimerBuilder(float timeToDestruction) {
        super(Aspect.all(DestructionTimer.class), 0);
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);
        DestructionTimer destTimer = entity.getComponent(DestructionTimer.class);
        destTimer.timeToDestruction = timeToDestruction;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return DestructionTimer.class;
    }
}
