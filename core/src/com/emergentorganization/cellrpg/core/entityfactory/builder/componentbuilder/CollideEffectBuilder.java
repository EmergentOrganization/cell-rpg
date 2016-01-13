package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.CollideEffect;


public class CollideEffectBuilder extends BaseComponentBuilder {
    private int collideDamage = 0;
    private int collideSelfDamage = 0;

    public CollideEffectBuilder() {
        super(Aspect.all(CollideEffect.class), 0);
    }

    public CollideEffectBuilder collideSelfDamage(int dam) {
        collideSelfDamage = dam;
        return this;
    }

    public CollideEffectBuilder collideDamage(int dam) {
        this.collideDamage = dam;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        CollideEffect eff = entity.getComponent(CollideEffect.class);
        eff.damage = collideDamage;
        eff.selfDamage = collideSelfDamage;
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return CollideEffect.class;
    }
}
