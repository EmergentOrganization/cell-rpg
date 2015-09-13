package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.entities.characters.Bullet;

/**
 * Created by OrelBitton on 06/06/2015.
 */
public class WeaponComponent extends EntityComponent {

    private MovementComponent mc;

    // TODO: weapon class
    private float speed = 100f;
    private float maxDist = 50f;
    private long delay = 200;

    private final int SHOT_CHARGE_COST = 25;
    private final float RECHARGE_RATE = 20f;
    private float recharge_remainder = 0f;

    private int charge = 100;
    private boolean charge_changed = true;
    private GraphicsComponent graphicsComponent;

    private long lastShot;
    private Vector2 vel = new Vector2();

    public WeaponComponent() {
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("100percent", "player-ammo/100p.png", 1, 1, 500);
        graphicsComponent.register("75percent", "player-ammo/75p.png", 1, 1, 500);
        graphicsComponent.register("50percent", "player-ammo/50p.png", 1, 1, 500);
        graphicsComponent.register("25percent", "player-ammo/25p.png", 1, 1, 500);
        graphicsComponent.play("100percent");
    }

    public int getCharge(){
        return charge;
    }

    @Override
    public void added() {
        mc = getFirstSiblingByType(MovementComponent.class);
        getEntity().addComponent(this.graphicsComponent);
    }

    public void shootTo(float x, float y) {
        if (lastShot == 0L)
            lastShot = TimeUtils.millis();

        // check if we can shoot right now
        if (TimeUtils.timeSinceMillis(lastShot) >= delay) {
            if (charge - SHOT_CHARGE_COST >= 0) {
                charge -= SHOT_CHARGE_COST;
                charge_changed = true;

                lastShot += delay;

                // get player position
                Vector2 pos = mc.getLocalPosition();

                // calculate the velocity
                vel.set(x, y).sub(pos).nor().scl(speed);

                addEntityToScene(new Bullet(pos, vel, maxDist));
            } // else not enough charge to shoot
        } // else trying to shoot too quickly
    }

    public void recharge(float rechargeTime){
        // applies recharge over given time
        recharge_remainder += rechargeTime*RECHARGE_RATE;
        charge += (int)recharge_remainder;  // add whole numbers to int
        recharge_remainder %= 1;  // save remainder for later
        charge_changed = true;

        if (charge > 100){
            charge = 100;
        }
    }

    public void update(float deltaTime){
        recharge(deltaTime);

        if (charge_changed) {
            // select texture based on health level of shield
            if (charge < 25) {
                graphicsComponent.play("25percent");
            } else if (charge < 50) {
                graphicsComponent.play("50percent");
            } else if (charge < 75) {
                graphicsComponent.play("75percent");
            } else {
                graphicsComponent.play("100percent");
            }
            charge_changed = false;
        }
    }
}
