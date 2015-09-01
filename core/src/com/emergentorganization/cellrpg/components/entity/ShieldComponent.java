package com.emergentorganization.cellrpg.components.entity;

import com.emergentorganization.cellrpg.components.EntityComponent;

/**
 * Created by 7yl4r on 2015-07-27
 */
public class ShieldComponent extends EntityComponent {

    private float health = 100f;
    private float divisor = 1f;  // damage reduction devisor (armor/defense value) (must be > 0)
    private boolean health_changed = true;
    private MovementComponent mc;
    private GraphicsComponent graphicsComponent;

    public ShieldComponent() {
        String sheetFileName = "";
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("100percent", "100p.png", 1, 1, 500);
        graphicsComponent.register("75percent", "75p.png", 1, 1, 500);
        graphicsComponent.register("50percent", "50p.png", 1, 1, 500);
        graphicsComponent.register("25percent", "25p.png", 1, 1, 500);
        graphicsComponent.play("100percent");
    }

    public void damage(final float amount){
        // deals damage to shield
        health -= amount/divisor;
        health_changed = true;
        if (health < 0){
            // TODO: getEntity().fireEvent(EntityEvents.SHIELD_DOWN);
        }
    }

    public void recharge(final float amount){
        health += amount/divisor;
        if (health > 100){
            health = 100;
        }
        health_changed = true;
    }

    @Override
    public void added() {
        mc = getFirstSiblingByType(MovementComponent.class);
        getEntity().addComponent(this.graphicsComponent);
    }

    @Override
    public void update(float deltaTime) {
        if (health_changed) {
            // select texture based on health level of shield
            if (health < 25) {
                graphicsComponent.play("25percent");
            } else if (health < 50) {
                graphicsComponent.play("50percent");
            } else if (health < 75) {
                graphicsComponent.play("75percent");
            } else {
                graphicsComponent.play("100percent");
            }
            health_changed = false;
        }
    }
}
