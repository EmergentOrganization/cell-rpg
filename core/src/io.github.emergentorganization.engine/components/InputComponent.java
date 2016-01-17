package io.github.emergentorganization.engine.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.input.player.MovementControls.MoveState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InputComponent extends Component {
    public final Vector2 direction = new Vector2();
    public float speed; // in.. some kind of unit
    public MoveState moveState = MoveState.NOT_MOVING;
    Logger logger = LogManager.getLogger(getClass());

    public void stopMoving() {
//        logger.trace("halt");
        direction.set(Vector2.Zero);
        moveState = MoveState.NOT_MOVING;
    }

    public Vector2 getForwardDirection(Rotation rot) {
        // returns direction vector facing in direction of entity up when has 0 rotation
        return new Vector2(0, 1).rotate(rot.angle);
    }
}
