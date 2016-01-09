package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.input.player.MovementControls.MoveState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by 7yl4r on 1/9/2016.
 */
public class AISystem extends DelayedIteratingSystem {
    Logger logger = LogManager.getLogger(getClass());
    ComponentMapper<AIComponent> AICom_m;
    ComponentMapper<InputComponent> inCom_m;
    ComponentMapper<Rotation> rot_m;

    public AISystem() {
        super(Aspect.all(AIComponent.class, InputComponent.class, Rotation.class));
    }

    /** Returns entity timer. */
    @Override
    protected float getRemainingDelay(int entityId)
    {
        return AICom_m.get(entityId).delay;
    }

    @Override
    public void processDelta(int entityId, float delt){
//        logger.trace("process delt " + delt + " ID:" + entityId);
        AICom_m.get(entityId).delay -= delt;
    }

    @Override
    public void processExpired(int entityId) {
//        logger.trace("running AI for ID#" + entityId);
        // do AI things with component

        // reset timer
        AIComponent ai = AICom_m.get(entityId);
        ai.delay = ai.period;

        // test: move forward TODO: use slightly more clever AI
        InputComponent input = inCom_m.get(entityId);
        input.moveState = MoveState.PATH_FOLLOW;
        Vector2 forwardDir = input.getForwardDirection(rot_m.get(entityId));
        input.direction.set(forwardDir);
    }
}
