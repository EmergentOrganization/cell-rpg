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
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.BaseCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

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
    private ComponentMapper<Bounds> bound_m;
    private ComponentMapper<Velocity> vel_m;
    private EventManager eventManager;

    public CAInteractionSystem(){
        super(Aspect.all(CAInteractionList.class, Position.class, Velocity.class));
    }

    @Override
    protected void inserted(int entityId) {
        Position pos = pos_m.get(entityId);
        CAInteractionList interacts = CAInteracdtions_m.get(entityId);
        Bounds bounds = bound_m.get(entityId);
        _inserted(pos, bounds, interacts);
    }

    protected void _inserted(Position pos, Bounds bounds, CAInteractionList interacts){
        interacts.lastCollisionPosition = pos.getCenter(bounds).cpy();
    }

    protected void processLayer(int collidingLayerId, CAInteractionList interList, Vector2 lastPosition,
                                final Vector2 currentPostion){
        logger.trace("checking layer");
        ArrayList<Integer> collidedStates = new ArrayList<Integer>();

        // get x, y of current position
        CAGridComponents gridComps = CAGridComp_m.get(collidingLayerId);
        int x = gridComps.getIndexOfX(currentPostion.x);
        int y = gridComps.getIndexOfY(currentPostion.y);
        CAInteraction interaction = interList.interactions.get(collidingLayerId);
        // check cells in colliding area
        int colliderRadius = interList.getColliderRadius(gridComps.cellSize);
        for (int dx = -colliderRadius; dx < colliderRadius; dx++){
            for (int dy = -colliderRadius; dy < colliderRadius; dy++){
                try {
                    BaseCell cell = gridComps.states[x+dx][y+dy];
                    int cellState = cell.state;  // copy int here to avoid concurrency issue
                    if (! collidedStates.contains(cellState)){ // if haven't already collided w/ this state
                        if(checkCollideAt(interaction, cell, currentPostion)){
                            collidedStates.add(cellState);  // don't check for this state collision again
                            if (cellState > 0) {
                                logger.trace("collided @ rel " + dx + "," + dy);
                                logger.trace("states not to check:" + collidedStates.toString());
                            }
                        }
                    }

                } catch(IndexOutOfBoundsException ex){
                    logger.trace("colliding cell out of bounds");
                    // don't check cells that are out-of-bounds
                }
            }
        }

        // TODO: check cells past through since last collision check
        // TODO: http://stackoverflow.com/questions/10350258/find-all-tiles-intersected-by-line-segment
    }

    protected void process(int entityId) {
        // process completed for each entity matching filter
        Bounds bounds = bound_m.get(entityId);
        Vector2 currentPostion = pos_m.get(entityId).getCenter(bounds);
        Vector2 velocity = vel_m.get(entityId).velocity;
        CAInteractionList interList = CAInteracdtions_m.get(entityId);

        logger.trace("CAInteractSys.process(" + entityId + ")");

        for (int colldingLayerId : interList.interactions.keySet()) {
            processLayer(colldingLayerId, interList, interList.lastCollisionPosition,
                    currentPostion);
        }
        interList.lastCollisionPosition.set(currentPostion);
    }

    @Override
    protected  void processSystem() {
        IntBag idBag = getEntityIds();
        for (int index = 0; index < idBag.size(); index ++ ) {
            int id = idBag.get(index);
            process(id);
        }
    }

    protected void applyCollision(CAInteraction inter, BaseCell cell, Vector2 pos){
        // impact the CA
        int state = cell.state;  // copy int here to avoid concurrency issue
        if (state > 0) {
            logger.trace("colliding w/ state " + state);
        }
        // fire events to entity
        if (inter.events.get(state) != null) {  // if some events
            logger.trace("firing events for state " + state);
            for (GameEvent evt : inter.events.get(state)) {
                eventManager.pushEvent(evt);
            }
        }
        if (inter.impacts.get(state) != null) {  // if some impacts
            if (state > 0) logger.trace("stamping @ " + pos);
            for (CAImpact imp : inter.impacts.get(state)) {
                CAGridComponents targetComps = CAGridComp_m.get(imp.targetGridId);
                targetComps.stampCenteredAt(imp.impactStamp, pos);
            }
        }
    }

    protected boolean checkCollideAt(CAInteraction inter, BaseCell cell, Vector2 pos) {
        if (inter.collidesWithState(cell.state)) {
            if (cell.state > 0) logger.trace("collide w/ state " + cell.state);
            applyCollision(inter, cell, pos);
            return true;
        } else {
            if (cell.state > 0) logger.trace(
                    "does not collide w/ state " + cell.state + ". "
                            + " colliding states are:" + inter.getCollidingStates()
            );
            return false;
        }
    }
}
