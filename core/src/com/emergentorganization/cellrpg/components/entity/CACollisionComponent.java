package com.emergentorganization.cellrpg.components.entity;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.entities.ca.CAGridBase;
import com.emergentorganization.cellrpg.entities.ca.NoBufferCAGrid;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * for allowing entities to "collide" with the CAGrid.
 *
 * create new CACollisionComponent, addCollision() for each effect you want, then addComponent().
 * Can affect the CAGrid onCollision using a gridStamp int[][].
 * Or can trigger EntityEvent to affect Entity.
 *
 * WARN: Current implementation cannot map two events (or two impacts) to one state+layer.
 *       Attempting to do so will break HashMap implementation.
 *       A relatively easy fix is possible for events using array if desired.
 *
 * Created by 7yl4r on 9/1/2015.
 */
public class CACollisionComponent extends EntityComponent {
    private MovementComponent mc;
    private Vector2 lastCollisionPosition;  // position @ last locatoin position was checked
    private ArrayList collidingStates = new ArrayList(); // list of states with collision effects
    private int colliderRadius;  // radius of collision object
    private int colliderGridSize;  // when checking the collision area, checks at grid intersections
    HashMap<Integer, int[][]> impacts = new HashMap<Integer, int[][]>(); // collision impact grid stamps
    HashMap<Integer, CALayer> impactLayers = new HashMap<Integer, CALayer>(); // layers impacted onCollision
    HashMap<Integer, EntityEvents> events = new HashMap<Integer, EntityEvents>(); // events triggered by collisions
    CALayer collidingLayer;

    public CACollisionComponent(CALayer colliding_layer){
        collidingLayer = colliding_layer;
    }

    public void addCollision(int state, EntityEvents triggeredEvent) {
        addCollision(state, triggeredEvent, 0, 0);
    }
    public void addCollision(int state, EntityEvents triggeredEvent, final int radius, final int gridSize){
        // adds collision between entity and cagrid layer
        // triggeredEvents: event triggered onCollision
        // state: ca state value collided with
        Integer stat = new Integer(state);
        if (!collidingStates.contains(stat)){
            collidingStates.add(new Integer(state));
        }
        events.put(stat, triggeredEvent);
        setCollisionSize(radius, gridSize);
    }

    public void addCollision(int state, int[][] collisionImpact, CALayer impacted_layer){
        addCollision(state, collisionImpact, impacted_layer, 0, 0);
    }
    public void addCollision(int state, int[][] collisionImpact, CALayer impacted_layer, final int radius, final int gridSize){
        // adds collision between entity and given layer
        // collisionImpact: gridSeedStamp to apply to layer at pt of collision
        // state: ca state value collided with
        Integer stat = new Integer(state);
        if (!collidingStates.contains(stat)){
            collidingStates.add(new Integer(state));
        }
        impacts.put(stat, collisionImpact);
        impactLayers.put(stat, impacted_layer);
        setCollisionSize(radius, gridSize);
    }

    private void setCollisionSize(final int radius, final int gridSize){
        assert (gridSize > 0);
        colliderRadius = radius;
        colliderGridSize = gridSize;
    }

    @Override
    public void added() {
        mc = getFirstSiblingByType(MovementComponent.class);
        lastCollisionPosition = mc.getWorldPosition();
    }

    @Override
    public void update(float deltaTime) {
        try {
            Vector2 currentPostion = mc.getWorldPosition();
            float delta = 1f/mc.getVelocity().len();  // this should check every pixel (assuming velocity is in pixels)
            float thresh = delta;
            Vector2 diff = new Vector2(lastCollisionPosition);
            while (diff.sub(currentPostion).len2() > thresh){
//                System.out.println("lastPos=" + lastCollisionPosition);
//                System.out.println("lerp("+currentPostion+","+delta+")");
                lastCollisionPosition.lerp(currentPostion, delta);
                System.out.println("collide @ " + lastCollisionPosition);
                collide(lastCollisionPosition);
                diff.set(lastCollisionPosition);
            }
        } catch(ClassCastException err){
            // non-cascene, can't check for ca collisions
            return;
        }
    }

    private boolean checkCollideAt(float x, float y){
        CAScene caScene = (CAScene) getEntity().getScene();
        CAGridBase layer = caScene.getLayer(collidingLayer);
        Integer state = new Integer(layer.getState(x, y));

        if (collidingStates.contains(state)){
            // impact the CA
            try{
                int[][] stamp = impacts.get(state);
                caScene.getLayer(impactLayers.get(state)).stampState(stamp, new Vector2(x,y));
            } catch(NullPointerException err){
                // maybe event-only interaction
            }
            // fire events to entity
            try{
                EntityEvents event = events.get(state);
                getEntity().fireEvent(event);
            } catch(NullPointerException err){
                // maybe impact-only interaction
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean collide(Vector2 position){
        // checks for collisions
        // returns true if collided, else false
        // this method isn't pretty, but it is most efficient...
        float x = position.x;
        float y = position.y;
        // check origin
        if (checkCollideAt(x, y)) return true;

        // check grid extending outwards from origin
        float delta = colliderGridSize;
        while(delta < colliderRadius){
            // x+1, y
            if(checkCollideAt(x+delta,y)) return true;
            // x-1, y
            if(checkCollideAt(x-delta,y)) return true;
            // x, y+1
            if(checkCollideAt(x,y+delta)) return true;
            // x, y-1
            if(checkCollideAt(x,y-delta)) return true;
            // check x+1, y+1
            if(checkCollideAt(x+delta,y+delta)) return true;
            // x+1, y-1
            if(checkCollideAt(x+delta,y-delta)) return true;
            // x-1, y+1
            if(checkCollideAt(x-delta,y+delta)) return true;
            // x-1, y-1
            if(checkCollideAt(x-delta,y-delta)) return true;

            delta += colliderGridSize;
        }  // else no collisions
        return false;
    }

}
