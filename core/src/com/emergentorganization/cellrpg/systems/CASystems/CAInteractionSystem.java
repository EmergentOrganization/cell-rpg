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

import java.util.HashMap;
import java.util.Map;

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
        // TODO: should the collision-checker stop after finding a single collision?
        // TODO: To accomplish this, cells should be checked immediately should keep list of already-checked.
        // TODO:    pros: more efficient, avoids multiple-events from simultaneous collisions
        // TODO:    cons: might leave some cells that should collide un-collided for fast-moving entities

        // STEP 1: collect list of cells to check. List incl cell_state, x, y. No duplicates (for efficiency).
        HashMap<CACellKey, BaseCell> cellsToCheck = new HashMap<CACellKey, BaseCell>();
        //         x                y       cell

        // STEP 2: add cells in current position's colliding area
        // get x, y of current position
        CAGridComponents gridComps = CAGridComp_m.get(collidingLayerId);
        int x = gridComps.getIndexOfX(currentPostion.x);
        int y = gridComps.getIndexOfY(currentPostion.y);
        // center of the colliding area
        try {
            cellsToCheck.put(new CACellKey(x, y, collidingLayerId), gridComps.states[x][y]);
        } catch (ArrayIndexOutOfBoundsException ex){
            // potentially colliding cell is outside of the ca grid
            logger.debug("collision-check cell is outside of ca grid");
            // ignore it
        }

        // add other cells in colliding area
        for (int dx = -interList.colliderRadius; dx < interList.colliderRadius; dx++){
            for (int dy = -interList.colliderRadius; dy < interList.colliderRadius; dy++){
                try {
                    CACellKey newKey = new CACellKey(x + dx, y + dy, collidingLayerId);
                    if (!cellsToCheck.containsKey(newKey)) {  // no duplicates!
                        cellsToCheck.put(newKey, gridComps.states[x + dx][y + dx]);
                    }
                } catch(IndexOutOfBoundsException ex){
                    // don't check cells that are out-of-bounds
                }
            }
        }

        // STEP 3: TODO: add cells past through since last collision check
        // TODO: http://stackoverflow.com/questions/10350258/find-all-tiles-intersected-by-line-segment
//        float delta = 1f/velocity.len();  // this should check every pixel (assuming velocity is in pixels)
//        float thresh = delta;
//
//        Vector2 diff = lastPosition.cpy().sub(currentPostion);
//
//        // check positions passed through since last check
//        while (diff.len2() > thresh){
//            logger.trace("lastPos=" + lastPosition);
//            logger.trace("lerp(" + currentPostion + "," + delta + ")");
//            lastPosition.lerp(currentPostion, delta);  // move to new position to check
//            logger.trace("check collide @ " + lastPosition);
//            boolean res = collide(lastPosition, interList, collidingLayerId);  // check new position
//            if (res) logger.info("collision @ " + currentPostion);
//            diff.set(lastPosition);
//        }

        // STEP 4: check each cell in list
        for (Map.Entry<CACellKey, BaseCell> entry : cellsToCheck.entrySet()){
            CACellKey key  = entry.getKey();
            BaseCell  cell = entry.getValue();

            checkCollideAt(interList.interactions.get(key.layer), cell, currentPostion);
        }
    }

    protected void process(int entityId) {
        // process completed for each entity matching filter
        Bounds bounds = bound_m.get(entityId);
        Vector2 currentPostion = pos_m.get(entityId).getCenter(bounds);
        Vector2 velocity = vel_m.get(entityId).velocity;
        CAInteractionList interList = CAInteracdtions_m.get(entityId);

        logger.trace("CAInteractSys.process(" + entityId + ")");

        for (int colldingLayerId : interList.interactions.keySet()) {
            processLayer(colldingLayerId, interList, interList.lastCollisionPosition.cpy(),
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
        if (inter.impacts.get(cell.state) != null) { // if some impacts
            for (CAImpact imp : inter.impacts.get(cell.state)) {
                CAGridComponents targetComps = CAGridComp_m.get(imp.targetGridId);
                targetComps.stampCenteredAt(imp.impactStamp, pos);
            }
        }

        // fire events to entity
        if (inter.events.get(cell.state) != null) {  // if some events
            for (GameEvent evt : inter.events.get(cell.state)) {
                eventManager.pushEvent(evt);
            }
        }
    }

    protected boolean checkCollideAt(CAInteraction inter, BaseCell cell, Vector2 pos) {
        if (inter.collidesWithState(cell.state)) {
            logger.trace("collide w/ state " + cell.state);
            applyCollision(inter, cell, pos);
            return true;
        } else {
            logger.trace(
                    "does not collide w/ state " + cell.state + ". "
                            + " colliding states are:" + inter.getCollidingStates()
            );
            return false;
        }
    }
}
