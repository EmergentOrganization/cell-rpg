package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.InputComponent;


public class InputBuilder extends BaseComponentBuilder {
    private float speed = 1f;

    public InputBuilder() {
        super(Aspect.all(InputComponent.class), 0);
    }

    /**
     * Defaults to 1 m/s
     */
    public InputBuilder speed(float movementSpeed) {
        this.speed = movementSpeed;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        InputComponent ic = entity.getComponent(InputComponent.class);
        ic.speed = speed; // meters per sec
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return InputComponent.class;
    }
}
