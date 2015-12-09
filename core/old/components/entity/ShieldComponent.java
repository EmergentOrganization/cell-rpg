package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.tools.FileStructure;

import java.io.File;

/**
 * Created by 7yl4r on 2015-07-27
 */
public class ShieldComponent extends EntityComponent {
    private final float RECHARGE_RATE_DELTA = .1f;
    private final Sound hurt;
    private final Sound shieldDown;
    private float rechargeRate = .1f;
    private float recharge_remainder = 0f;

    private float DAMAGE_AMOUNT = 10f;
    private float health = 100f;
    private float divisor = 1f;  // damage reduction devisor (armor/defense value) (must be > 0)
    private boolean health_changed = true;
    private MovementComponent mc;
    private GraphicsComponent graphicsComponent;

    public ShieldComponent() {
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("100percent", "game/shield/100p");
        graphicsComponent.register("75percent", "game/shield/75p");
        graphicsComponent.register("50percent", "game/shield/50p");
        graphicsComponent.register("25percent", "game/shield/25p");
        graphicsComponent.play("100percent");

        AssetManager assets = CellRpg.fetch().getAssetManager();
        hurt = assets.get(FileStructure.RESOURCE_DIR + "sounds/PlayerHurt.wav", Sound.class);
        shieldDown = assets.get(FileStructure.RESOURCE_DIR + "sounds/ShieldDown.wav", Sound.class);
    }

    public float getHealth(){
        return health;
    }

    public void damage(){
        // deals damage to shield
        health -= DAMAGE_AMOUNT/divisor;
        health_changed = true;
        if (health < 0){
            shieldDown.play();
            getEntity().fireEvent(EntityEvents.SHIELD_DOWN);
        }
        hurt.play();
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

    public void addEnergy(final float amount){
        health += amount/divisor;
        if (health > 100){
            health = 100;
        }
        health_changed = true;
    }


    private void recharge(float rechargeTime){
        // applies recharge over given time
        recharge_remainder += rechargeTime* rechargeRate;
        addEnergy((int)recharge_remainder);  // add whole numbers to int
        recharge_remainder %= 1;  // save remainder for later
    }

    @Override
    public void update(float deltaTime) {
        recharge(deltaTime);
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

    @Override
    public void added() {
        mc = getFirstSiblingByType(MovementComponent.class);
        getEntity().addComponent(this.graphicsComponent);
    }
}
