package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by OrelBitton on 07/06/2015.
 */
public class BulletComponent extends BaseComponent {

    private MovementComponent mc;

    private Vector2 shootingPos;
    private float angle, speed, distance;

    public BulletComponent(Vector2 shootingPos, float angle, float speed, float distance){
        this.shootingPos = shootingPos;
        this.angle = angle;
        this.speed = speed;
        this.distance = distance;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);

        mc.setRotation(angle);
    }

    public void update(){

    }
}
