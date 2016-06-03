package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.AIComponent;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import io.github.emergentorganization.cellrpg.core.components.*;
import io.github.emergentorganization.cellrpg.input.player.MovementControls.MoveState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

@Profile(using=EmergentProfiler.class, enabled=true)
public class AISystem extends DelayedIteratingSystem {
    Logger logger = LogManager.getLogger(getClass());
    ComponentMapper<AIComponent> AICom_m;
    ComponentMapper<InputComponent> inCom_m;
    ComponentMapper<Rotation> rot_m;
    ComponentMapper<Lifecycle> life_m;
    ComponentMapper<Position> pos_m;

    public AISystem() {
        super(Aspect.all(AIComponent.class));
    }

    /**
     * Returns entity timer.
     */
    @Override
    protected float getRemainingDelay(int entityId) {
        float res = AICom_m.get(entityId).delay;
        logger.trace(res + "s delay remaining for ent#" + entityId);
        return res;
    }

    @Override
    public void processDelta(int entityId, float delt) {
        logger.trace("process delt " + delt + " ID:" + entityId);
        AICom_m.get(entityId).delay -= delt;
    }

    @Override
    public void processExpired(int entityId) {
        logger.trace("running AI for ID#" + entityId);
        // do AI things with component
        AIComponent ai = AICom_m.get(entityId);
        try {
            switch (ai.type) {
                case DUMBWALK:
                    dumbWalk(entityId);
                    break;
                case RANDWALK:
                    randWalk(entityId);
                    break;
                case CHASE:
                    chase(entityId);
                    break;
                default:
                    logger.error("AI type not recognized for ent#" + entityId);
            }
        } catch (NullPointerException ex){
            logger.error("ERR: cannot process AI for ent#" + entityId, ex);
            life_m.get(entityId).kill();
        }
        ai.delay = ai.period;
        super.offerDelay(ai.period);
    }

    private void chase(int entityId){
        // chases targetEntity
        Vector2 targPos;
        try {
            AIComponent AIC = AICom_m.get(entityId);
            targPos = AIC.target.getComponent(Position.class).position;
        } catch (NullPointerException ex){
            logger.error("chase entity not set or does not have position for ent#" + entityId, ex);
            return;
        }
        InputComponent input = inCom_m.get(entityId);
        input.moveState = MoveState.PATH_FOLLOW;

        input.direction.set(targPos.cpy().sub(pos_m.get(entityId).position));
    }

    private void randWalk(int entityId) {
        // moves randomly up, down, left, or right
        InputComponent input = inCom_m.get(entityId);
        input.moveState = MoveState.PATH_FOLLOW;
        // rand -1, 0, 1
        int choice = ThreadLocalRandom.current().nextInt(0, 3 + 1);
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
            default:
                throw new IllegalStateException("illegal random int in randWalk");
        }

        input.direction.add(x, y).nor();
    }

    private void dumbWalk(int entityId) {
        // test AI just moves forward
        InputComponent input = inCom_m.get(entityId);
        input.moveState = MoveState.PATH_FOLLOW;
        Vector2 forwardDir = input.getForwardDirection(rot_m.get(entityId));
        input.direction.set(forwardDir);
    }

    @Override
    public void initialize(){
        super.initialize();
    }

    @Override
    public void begin(){
        super.begin();
    }

    @Override
    public void end(){
        super.end();
    }
}
