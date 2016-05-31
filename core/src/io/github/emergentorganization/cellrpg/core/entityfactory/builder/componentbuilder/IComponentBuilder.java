package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Component;
import com.artemis.Entity;


public interface IComponentBuilder {
    void build(Entity entity);

    boolean isBuilt();

    int getSortIndex();

    Class<? extends Component> getComponentClass();
}
