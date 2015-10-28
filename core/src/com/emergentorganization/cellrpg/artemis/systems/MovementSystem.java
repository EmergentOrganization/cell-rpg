package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.artemis.components.Position;
import com.emergentorganization.cellrpg.artemis.components.Velocity;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;

/**
 * Created by brian on 10/28/15.
 */
@Wire
public class MovementSystem extends IteratingSystem {

    private ComponentMapper<Position> pm;
    private ComponentMapper<Velocity> vm;

    public MovementSystem() {
        super(Aspect.all(Position.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        Position p = pm.get(entityId);
        Velocity v = vm.get(entityId);

        float d = world.getDelta();
        p.position.add(v.velocity.x * d, v.velocity.y * d);
    }
}
