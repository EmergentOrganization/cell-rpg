package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.artemis.components.Position;
import com.emergentorganization.cellrpg.artemis.components.Velocity;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;

/**
 * Created by brian on 10/28/15.
 */
public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Aspect.all(Position.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {

    }
}
