package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.AIComponent;


public class AIComponentBuilder extends BaseComponentBuilder {
    private AIComponent.aiType AIType;
    private float AIPeriod = 1f;

    public AIComponentBuilder(AIComponent.aiType aiType) {
        super(Aspect.all(AIComponent.class), 0);

        this.AIType = aiType;
    }

    public AIComponentBuilder AIPeriod(float period) {
        AIPeriod = period;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        AIComponent ai = entity.getComponent(AIComponent.class);
        ai.type = AIType;
        ai.period = AIPeriod;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return AIComponent.class;
    }
}
