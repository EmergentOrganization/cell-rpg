package com.emergentorganization.cellrpg.components.entity;

import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.entities.CAGrid;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class CACollisionComponent extends EntityComponent {
    private MovementComponent mc;

    public CACollisionComponent(){

    }

    public void addCollision(CALayer layer, int states, EntityEvents triggeredEvent){
        // adds collision between entity and given layer
        // triggeredEvents: event triggered onCollision
        // states: ca state value collided with
        // TODO
    }

    public void addCollision(CALayer layer, int states, int[][] collisionImpact){
        // adds collision between entity and given layer
        // collisionImpact: gridSeedStamp to apply to layer at pt of collision
        // states: ca state value collided with
        // TODO
    }

    public void addCollision(CALayer layer, int states, int[][] collisionImpact, EntityEvents triggeredEvent){
        // adds collision between entity and given layer
        // collisionImpact: gridSeedStamp to apply to layer at pt of collision
        // triggeredEvents: event triggered onCollision
        // states: ca state value collided with
        // TODO
    }

    @Override
    public void added() {
        mc = getFirstSiblingByType(MovementComponent.class);
    }

    @Override
    public void update(float deltaTime) {
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
        // TODO: modify this to use lists created by addCollision()
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
