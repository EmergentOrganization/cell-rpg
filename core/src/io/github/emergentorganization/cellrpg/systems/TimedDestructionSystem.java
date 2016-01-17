package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.emergentorganization.cellrpg.components.DestructionTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// TODO: use DelayedIteratingSystem instead
public class TimedDestructionSystem extends IntervalEntityProcessingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    public TimedDestructionSystem() {
        super(Aspect.all(DestructionTimer.class), 1);
    }

    public void process(Entity e) {
        DestructionTimer destTimer = e.getComponent(DestructionTimer.class);
        logger.trace("timedDestruction has " + destTimer.timeToDestruction + "s left.");
        if (destTimer.timeToDestruction < 0) {
            return;
        } else if (destTimer.timeToDestruction < 1) {
            try {
                world.deleteEntity(e);
            } catch (NullPointerException ex) {
                // entity may be already deleted
                logger.trace("timed destruction fail; entity already deleted?");
                return;
            }
        } else {
            destTimer.timeToDestruction -= 1;
        }
    }
}
