package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.artemis.components.Input;
import com.emergentorganization.cellrpg.artemis.components.Velocity;

/**
 * Created by brian on 10/28/15.
 */
public class InputSystem extends IteratingSystem {

    public InputSystem() {
        super(Aspect.all(Input.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {

    }
}
