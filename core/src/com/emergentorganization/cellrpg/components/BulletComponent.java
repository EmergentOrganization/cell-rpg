package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by OrelBitton on 07/06/2015.
 */
public class BulletComponent extends BaseComponent {

    private MovementComponent mc;

    private Vector2 shootingPos, dest;
    private float velocity;

    public BulletComponent(Vector2 shootingPos, Vector2 dest, float velocity){
        type = ComponentType.BULLET;

        this.shootingPos = shootingPos;
        this.dest = dest;
        this.velocity = velocity;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);

        // TODO: translation from local pos to world pos
        mc.setWorldPosition(shootingPos);
        mc.setVelocity(velocity);
        mc.moveTo(dest.x, dest.y);

        // TODO: destroy on arrival using ArrivedToDestination message.
    }

}
