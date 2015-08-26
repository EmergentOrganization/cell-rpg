package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.entities.CAGrid;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;

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
        if(collide()){
            // if bullet has collided
            System.out.println("collided!");
            removeEntityFromScene(getEntity());
        }
    }

    private boolean collide(){
        // checks for collisions
        // returns true if collided, else false
        CAScene caScene = (CAScene) getEntity().getScene();
        CAGrid layer = caScene.getLayer(CALayer.VYROIDS);
        int state = layer.getState(mc.getWorldPosition().x, mc.getWorldPosition().y);
        // this is a switch statement so we can do things with different states if desired
        int[][] collideEffect;
        switch (state){
            case 0:
                return false;
            default:
                collideEffect = new int[][] {{0,0,0},{0,0,0},{0,0,0}};  // this stamp just destroys the cell
                layer.stampState(collideEffect,mc.getWorldPosition());
                return true;
        }

    }
}
