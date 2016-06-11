package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;


public class SpontaneousGenerationListBuilder extends BaseComponentBuilder {
    private final float radius;

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
