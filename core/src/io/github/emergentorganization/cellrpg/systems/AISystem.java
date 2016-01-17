package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.AIComponent;
import io.github.emergentorganization.engine.components.InputComponent;
import io.github.emergentorganization.engine.components.Rotation;
import io.github.emergentorganization.cellrpg.input.player.MovementControls.MoveState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;


public class AISystem extends DelayedIteratingSystem {
    Logger logger = LogManager.getLogger(getClass());
    ComponentMapper<AIComponent> AICom_m;
    ComponentMapper<InputComponent> inCom_m;
    ComponentMapper<Rotation> rot_m;

    public AISystem() {
        super(Aspect.all(AIComponent.class, InputComponent.class, Rotation.class));
    }

    /**
     * Returns entity timer.
     */
    @Override
    protected float getRemainingDelay(int entityId) {
        return AICom_m.get(entityId).delay;
    }

    @Override
    public void processDelta(int entityId, float delt) {
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

        switch (ai.type) {
            case DUMBWALK:
                dumbWalk(entityId);
                break;
            case RANDWALK:
                randWalk(entityId);
                break;
        }
    }

    private void randWalk(int entityId) {
        // moves randomly up, down, left, or right
        InputComponent input = inCom_m.get(entityId);
        input.moveState = MoveState.PATH_FOLLOW;
        // rand -1, 0, 1
        int choice = ThreadLocalRandom.current().nextInt(0, 4 + 1);
        float x = 0, y = 0;
        switch (choice) {
            case 0:
                x = 1;
                break;
            case 1:
                x = -1;
                break;
            case 2:
                y = 1;
                break;
            case 3:
                y = -1;
                break;
        }

        input.direction.set(new Vector2(x, y));
    }

    private void dumbWalk(int entityId) {
        // test AI just moves forward
        InputComponent input = inCom_m.get(entityId);
        input.moveState = MoveState.PATH_FOLLOW;
        Vector2 forwardDir = input.getForwardDirection(rot_m.get(entityId));
        input.direction.set(forwardDir);
    }
}
