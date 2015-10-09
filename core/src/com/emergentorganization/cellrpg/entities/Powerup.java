package com.emergentorganization.cellrpg.entities;

import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.WeaponComponent;
import com.emergentorganization.cellrpg.scenes.arcadeScore;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 7yl4r on 2015-09-27.
 */
public abstract class Powerup extends Entity {
    private float COLLECTION_RADIUS = 3;
    private float LIFETIME = 5;
    private int POINTS_FOR_COLLECTING = 1000;
    static final float MIN_SCALE = .01f;  // (for NYI shrinking)
    private final GraphicsComponent graphicsComponent;
    private MovementComponent playerMC;
    private MovementComponent mc;
    private float life = 0;

    public Powerup(String graphicsId){
        super(ZIndex.PROJECTILE);
        graphicsComponent = new GraphicsComponent();
        graphicsComponent.register("idle", graphicsId);
        graphicsComponent.play("idle");
        addComponent(this.graphicsComponent);
        mc = getFirstComponentByType(MovementComponent.class);
    }

    /* === ABSTRACT METHODS === */
    protected abstract void applyPowerupCollection();
    // applies whatever effects powerup should have.

    /* === PUBLIC METHODS === */
    public Powerup setLifetime(int newLife){
        // sets lifetime, returns self for chaining
        LIFETIME = newLife;
        return this;
    }

    public Powerup setCollectRadius(int newRadius){
        // sets collection radius, returns self for chaining
        COLLECTION_RADIUS = newRadius;
        return this;
    }

    public Powerup setCollectionPoints(int newPoints){
        // sets number of points awarded for collection, returns self for chaining
        POINTS_FOR_COLLECTING = newPoints;
        return this;
    }

    /* === PRIVATE METHODS === */
    private void collectPowerup(){
        if(getScene() instanceof arcadeScore){
            ((arcadeScore) getScene()).addPoints(POINTS_FOR_COLLECTING);
        }
        applyPowerupCollection();
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
            // TODO: shrink?
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

