package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.components.Input;
import com.emergentorganization.cellrpg.components.Velocity;

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
