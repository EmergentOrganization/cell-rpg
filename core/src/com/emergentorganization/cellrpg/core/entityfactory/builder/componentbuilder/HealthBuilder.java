package com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.emergentorganization.cellrpg.components.Health;

/**
 * Created by Brian on 1/11/2016.
 */
public class HealthBuilder extends BaseComponentBuilder {
    private int health = Integer.MAX_VALUE;  // still destructible TODO: fix?
    private int maxHealth = -1;  // defaults to full health unless other given.

    public HealthBuilder() {
        super(Aspect.all(Health.class), 50);
    }

    public HealthBuilder maxHealth( int maxHealth){
        this.maxHealth = maxHealth;
        return this;
    }

    public HealthBuilder health( int health){
        this.health = health;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        Health heal = entity.getComponent(Health.class);
        heal.health = health;

        if (maxHealth > -1){
            heal.maxHealth = maxHealth;
        } else {
            heal.maxHealth = health;
        }
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return Health.class;
    }
}
