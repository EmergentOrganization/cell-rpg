package io.github.emergentorganization.cellrpg.components.CAInteraction;

import com.badlogic.gdx.utils.IntMap;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines interactions with ONE colliding ca grid layer.
 */
public class CAInteraction {
    private final Logger logger = LogManager.getLogger(getClass());
    // collision impact grid stamps (maps state to list of impact/layer pairs):
    public IntMap<List<CAImpact>> impacts = new IntMap<List<CAImpact>>();
    // events triggered by collisions (maps state to list of events):
    public IntMap<List<GameEvent>> events = new IntMap<List<GameEvent>>();
    private ArrayList collidingStates = new ArrayList(); // list of states with collision effects

    public CAInteraction() {

    }

    public ArrayList getCollidingStates() {
        return collidingStates;
    }

    public CAInteraction addCollisionImpactStamp(int state, int[][] collisionImpact, int targetLayerId) {
        return addCollisionImpactStamp(state, collisionImpact, targetLayerId, 0);
    }

    public CAInteraction addCollisionImpactStamp(int state, int[][] collisionImpact, int targetLayerId, int period) {
        // adds collision with given state which stamps targeted layer with the collisionImpact state matrix.
        // collisionImpact: gridSeedStamp to apply to layer at pt of collision
        // state: ca state value collided with
        // targetLayerId: Id of the layer that will be stamped
        // period: number of generations to wait in between stamp applications
        setupCollideWithState(state);

        if (!impacts.containsKey(state)) {
            impacts.put(state, new ArrayList<CAImpact>());
        }
        impacts.get(state).add(new CAImpact(targetLayerId, collisionImpact, period));

        return this;
    }

    public CAInteraction addEventTrigger(int state, GameEvent triggeredEvent) {
        // adds collision between entity and cagrid layer
        // triggeredEvents: event triggered onCollision
        // state: ca state value collided with
        setupCollideWithState(state);
        if (!events.containsKey(state)) {
            events.put(state, new ArrayList<GameEvent>());
        }
        events.get(state).add(triggeredEvent);
        return this;
    }

    public boolean collidesWithState(final int state) {
        return collidingStates.contains(state);
    }

    private void setupCollideWithState(int state) {
        // ensures given state will be checked for collisions
        // (all states are not checked by default for efficiency)
        Integer stat = new Integer(state);
        if (!collidingStates.contains(stat)) {
            logger.trace("new colliding state: " + stat);
            collidingStates.add(stat);
        } else {
            logger.trace("already collides w/ state: " + stat);
        }
        logger.trace("colliding states: " + collidingStates);
    }
}
