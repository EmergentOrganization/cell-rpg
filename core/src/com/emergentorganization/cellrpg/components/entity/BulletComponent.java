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
        this.start = start;
        this.velocity = velocity;
        this.maxDist = maxDist;
    }

    @Override
    public void added() {
        mc = getFirstSiblingByType(MovementComponent.class);

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

    private boolean checkCollideAt(float x, float y){
        CAScene caScene = (CAScene) getEntity().getScene();
        CAGrid layer = caScene.getLayer(CALayer.VYROIDS);
        int state = layer.getState(x, y);
        // this is a switch statement so we can do things with different states if desired
        int[][] collideEffect;
        switch (state){
            case 0:
                collideEffect = new int[][] {
                        {0,1,0},
                        {1,0,1},
                        {0,1,0},
                };
                caScene.getLayer(CALayer.ENERGY).stampState(collideEffect,mc.getWorldPosition());
                return false;
            default:
                collideEffect = new int[][] {
                        {0,0,0,0,0},
                        {0,0,0,0,0},
                        {0,0,0,0,0},
                        {0,0,0,0,0},
                        {0,0,0,0,0}
                };  // this stamp destroys the cells nearby
                caScene.getLayer(CALayer.VYROIDS).stampState(collideEffect,mc.getWorldPosition());
                return true;
        }
    }

    private boolean collide(){
        // checks for collisions
        // returns true if collided, else false
        float bulletSize = 10f;
        float x = mc.getWorldPosition().x;
        float y = mc.getWorldPosition().y;
        return checkCollideAt(x,y);
        //return checkCollideAt(x-bulletSize, y-bulletSize) && checkCollideAt(x-bulletSize, y+bulletSize) && checkCollideAt(x+bulletSize, y-bulletSize) && checkCollideAt(x+bulletSize, y+bulletSize);
    }
}
