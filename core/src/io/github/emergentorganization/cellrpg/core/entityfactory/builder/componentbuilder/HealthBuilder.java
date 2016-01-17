package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.components.Health;


public class HealthBuilder extends BaseComponentBuilder {
    private int health;
    private int maxHealth;  // defaults to full health unless other given.

    public HealthBuilder(int health) {
        super(Aspect.all(Health.class), 50);
        this.health = health;
    }

    public HealthBuilder maxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    @Override
    public void build(Entity entity) {
        super.build(entity);

        Health heal = entity.getComponent(Health.class);
        heal.health = health;

        if (maxHealth > 0) {
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
