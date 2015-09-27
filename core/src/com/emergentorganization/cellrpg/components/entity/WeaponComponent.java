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
    private long delay = 100;

    private final int SHOT_CHARGE_COST = 30;
    private final float RECHARGE_RATE_DELTA = 5f;
    private float rechargeRate = 20f;
    private float recharge_remainder = 0f;

    private int charge = 100;
    private boolean charge_changed = true;
    private GraphicsComponent graphicsComponent;

    private long lastShot;
    private Vector2 vel = new Vector2();

    public WeaponComponent() {
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("100percent", "game/player-ammo/100p");
        graphicsComponent.register("75percent", "game/player-ammo/75p");
        graphicsComponent.register("50percent", "game/player-ammo/50p");
        graphicsComponent.register("25percent", "game/player-ammo/25p");
        graphicsComponent.play("100percent");
    }

    public void increaseRechargeRate(final int numberOfTimes){
        for (int i = 0; i < numberOfTimes; i++){
            increaseRechargeRate();
        }
    }

    public void decreaseRechargeRate(final int numberOfTimes){
        for (int i = 0; i < numberOfTimes; i++){
            decreaseRechargeRate();
        }
    }

    public float getRechargeRate(){
        return rechargeRate;
    }

    public void decreaseRechargeRate(){
        rechargeRate -= RECHARGE_RATE_DELTA;
    }

    public void increaseRechargeRate(){
        rechargeRate += RECHARGE_RATE_DELTA;
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
        recharge_remainder += rechargeTime* rechargeRate;
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
