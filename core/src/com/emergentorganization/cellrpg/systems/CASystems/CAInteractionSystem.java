package com.emergentorganization.cellrpg.systems.CASystems;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.components.CAInteraction.CAImpact;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteraction;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteractionList;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.managers.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * for allowing entities to "collide" with the CAGrid.
 *
 * create new CAInteractionSystem, addCollision() for each effect you want, then addComponent().
 * Can affect the CAGrid onCollision using a gridStamp int[][].
 * Or can trigger EntityEvent to affect Entity.
 *
 * WARN: Current implementation cannot map two events (or two impacts) to one state+layer.
 *       Attempting to do so will break HashMap implementation.
 *       A relatively easy fix is possible for events using array if desired.
 *
 * Ported from CACollisionComponent by 7yl4r on 2015-12-08
 */
public class CAInteractionSystem extends BaseEntitySystem {
    private final Logger logger = LogManager.getLogger(getClass());

    // artemis-injected entity components:
    private ComponentMapper<CAInteractionList> CAInteracdtions_m;
    private ComponentMapper<CAGridComponents> CAGridComp_m;
    private ComponentMapper<Position> pos_m;
    private ComponentMapper<Velocity> vel_m;
    private EventManager eventManager;

    public CAInteractionSystem(){
        super(Aspect.all(CAInteractionList.class, Position.class, Velocity.class));
    }

    @Override
    protected void inserted(int entityId) {
        Position pos = pos_m.get(entityId);
        CAInteractionList interacts = CAInteracdtions_m.get(entityId);
        _inserted(pos, interacts);
    }

    protected void _inserted(Position pos, CAInteractionList interacts){
        interacts.lastCollisionPosition = pos.position.cpy();
    }

    protected void processLayer(int collidingLayerId, CAInteractionList interList,
                                final Vector2 currentPostion, final Vector2 velocity, Vector2 diff){
        float delta = 1f/velocity.len();  // this should check every pixel (assuming velocity is in pixels)
        float thresh = delta;

        while (diff.sub(currentPostion).len2() > thresh){
//                System.out.println("lastPos=" + lastCollisionPosition);
//                System.out.println("lerp("+currentPostion+","+delta+")");
            interList.lastCollisionPosition.lerp(currentPostion, delta);
//                System.out.println("collide @ " + lastCollisionPosition);
            boolean res = collide(interList.lastCollisionPosition, interList, collidingLayerId);
            if (res) logger.info("collision @ " + currentPostion );
            diff.set(interList.lastCollisionPosition);
        }
    }

    protected void process(int entityId) {
        // process completed for each entity matching filter
        Vector2 currentPostion = pos_m.get(entityId).position;
        Vector2 velocity = vel_m.get(entityId).velocity;
        CAInteractionList interList = CAInteracdtions_m.get(entityId);

//        logger.info("CAInteractSys.process(" + entityId + ")");

        for (int colldingLayerId : interList.interactions.keySet()) {
            processLayer(colldingLayerId, interList, currentPostion, velocity, new Vector2(interList.lastCollisionPosition));
        }
    }

    @Override
    protected  void processSystem() {
        IntBag idBag = getEntityIds();
        for (int index = 0; index < idBag.size(); index ++ ) {
            int id = idBag.get(index);
            process(id);
        }
    }

    protected boolean checkCollideAt(Vector2 pos, CAInteraction inter, CAGridComponents gridComps){
        // performs collision between given object and colliding layer at given position
//        logger.info("checkCollide gridId#" + collidingLayerId + " @ " + pos);
        int state = gridComps.getState(pos);

        if (inter.collidesWithState(state)){
            logger.trace("collide w/ state " + state);
            // impact the CA
            if (inter.impacts.get(state) != null) { // if some impacts
                for (CAImpact imp : inter.impacts.get(state)) {
                    CAGridComp_m.get(imp.targetGridId).stampState(imp.impactStamp, pos);
                }
            }

            // fire events to entity
            if (inter.events.get(state) != null) {  // if some events
                for (GameEvent evt : inter.events.get(state)) {
                    eventManager.pushEvent(evt);
                }
            }
            return true;
        } else {
            logger.trace(
                    "does not collide w/ state " + state + ". "
                    + " colliding states are:" + inter.getCollidingStates()
            );
            return false;
        }
    }

    private boolean collide(Vector2 position, CAInteractionList interList, int collidingLayerId){
        // checks for collisions
        // returns true if collided, else false
        CAInteraction inter =  interList.interactions.get(collidingLayerId);
        CAGridComponents gridComps = CAGridComp_m.get(collidingLayerId);

        float x = position.x;
        float y = position.y;
        // check origin
        if (checkCollideAt(position, inter, gridComps)) return true;

        // check grid extending outwards from origin
        float delta = interList.colliderGridSize;
        while(delta < interList.colliderRadius){
            // x+1, y
            if(checkCollideAt(new Vector2(x+delta, y     ), inter, gridComps)) return true;
            // x-1, y
            if(checkCollideAt(new Vector2(x-delta, y     ), inter, gridComps)) return true;
            // x  , y+1
            if(checkCollideAt(new Vector2(x      ,y+delta), inter, gridComps)) return true;
            // x  , y-1
            if(checkCollideAt(new Vector2(x      ,y-delta), inter, gridComps)) return true;
            // x+1, y+1
            if(checkCollideAt(new Vector2(x+delta,y+delta), inter, gridComps)) return true;
            // x+1, y-1
            if(checkCollideAt(new Vector2(x+delta,y-delta), inter, gridComps)) return true;
            // x-1, y+1
            if(checkCollideAt(new Vector2(x-delta,y+delta), inter, gridComps)) return true;
            // x-1, y-1
            if(checkCollideAt(new Vector2(x-delta,y-delta), inter, gridComps)) return true;

            delta += interList.colliderGridSize;
        }  // else no collisions
        return false;
    }

}
