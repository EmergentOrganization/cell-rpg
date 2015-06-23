package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;

/**
 * Created by OrelBitton on 07/06/2015.
 */
public class BulletComponent extends EntityComponent {

    private MovementComponent mc;

    private Vector2 start, velocity;
    private float maxDist;

    public BulletComponent(Vector2 start, Vector2 velocity, float maxDist){
        type = ComponentType.BULLET;

        this.start = start;
        this.velocity = velocity;
        this.maxDist = maxDist;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);

        // TODO: translation from local pos to world pos
        mc.setWorldPosition(start);
        mc.setVelocity(velocity);
    }

    @Override
    public void update(float deltaTime) {
        if(mc.getWorldPosition().dst(start) >= maxDist){
            removeEntityFromScene(getEntity());
        }

    }
}
