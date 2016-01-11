package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;

/**
 * Created by Brian on 1/11/2016.
 */
public class SpontaneousGenerationListBuilder extends BaseComponentBuilder {
    private float radius;

    public SpontaneousGenerationListBuilder(float radius) {
        super(Aspect.all(SpontaneousGenerationList.class), 0);
        this.radius = radius;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        SpontaneousGenerationList genList = entity.getComponent(SpontaneousGenerationList.class);
        genList.radius = this.radius;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return SpontaneousGenerationList.class;
    }
}
