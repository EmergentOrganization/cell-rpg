package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.emergentorganization.cellrpg.components.destructionTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by 7yl4r on 1/2/2016.
 */
public class TimedDestructionSystem extends IntervalEntityProcessingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    public TimedDestructionSystem(){
        super(Aspect.all(destructionTimer.class), 1);
    }

    public void process(Entity e){
        destructionTimer destTimer = e.getComponent(destructionTimer.class);
        logger.trace("timedDestruction has " + destTimer.timeToDestruction + "s left.");
        if (destTimer.timeToDestruction < 0){
            return;
        } else if (destTimer.timeToDestruction < 1){
            world.deleteEntity(e);
        } else {
            destTimer.timeToDestruction -= 1;
        }
    }
}
