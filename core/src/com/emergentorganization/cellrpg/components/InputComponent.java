package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.input.MoveState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by brian on 10/28/15.
 */
public class InputComponent extends Component {
    Logger logger = LogManager.getLogger(getClass());

    public final Vector2 direction = new Vector2();
    public int controlScheme; // TODO placeholder
    public float speed; // in.. some kind of unit
    public MoveState moveState = MoveState.NOT_MOVING;

    public void stopMoving() {
//        logger.trace("halt");
        direction.set(Vector2.Zero);
        moveState = MoveState.NOT_MOVING;
    }
}
