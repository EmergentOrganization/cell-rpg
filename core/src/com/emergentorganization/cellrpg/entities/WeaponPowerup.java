package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.*;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.arcadeScore;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 7yl4r on 2015-09-27.
 */
public class WeaponPowerup extends Entity {
    static final int POINTS_FOR_COLLECTING = 1000;
    static final float COLLECTION_RADIUS = 3;
    static final float LIFETIME = 5;
    static final float MIN_SCALE = .01f;
    private final GraphicsComponent graphicsComponent;
    private MovementComponent playerMC;
    private MovementComponent mc;
    private float life = 0;

    public WeaponPowerup(){
        super(ZIndex.PROJECTILE);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", "game/powerup-plus");
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
        mc = getFirstComponentByType(MovementComponent.class);
    }

    private void collectPowerup(){
        if(getScene() instanceof arcadeScore){
            ((arcadeScore) getScene()).addPoints(POINTS_FOR_COLLECTING);
        }
        // temporary big increase
        getScene().getPlayer().getFirstComponentByType(WeaponComponent.class).increaseRechargeRate(10);

        // then scale back to just a small increase
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getScene().getPlayer().getFirstComponentByType(WeaponComponent.class).decreaseRechargeRate(9);
            }
        }, 7*1000);  // 7s
        dispose();
    }

    @Override
    public void dispose(){
        super.dispose();
        mc.setWorldPosition(-1000, -1000); // TODO: this is temporary hack. why does dispose() not destroy sprite & stop update() calls?
    }

    @Override
    public void added(){
        super.added();
        playerMC = getScene().getPlayer().getFirstComponentByType(MovementComponent.class);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        // check for player collected powerup
        if (mc.getWorldPosition().dst(playerMC.getWorldPosition()) < COLLECTION_RADIUS ){
            collectPowerup();
        }

        // check for powerup out of time
        life += deltaTime;
        if (life > LIFETIME){
            // TODO: shrink
            //graphicsComponent.getSprite().setScale(
            //        graphicsComponent.getSprite().getScaleX() / (life - LIFETIME),
            //        graphicsComponent.getSprite().getScaleY() / (life - LIFETIME)
            //);
            //if (graphicsComponent.getSprite().getScaleX() < MIN_SCALE){
            // poof, it's gone!
            dispose();

        }
    }

}

