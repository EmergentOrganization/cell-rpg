package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.emergentorganization.cellrpg.components.Input;
import com.emergentorganization.cellrpg.components.PhysicsBody;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Velocity;
import com.emergentorganization.cellrpg.managers.BodyManager;

/**
 * Created by brian on 10/28/15.
 */
@Wire
public class MovementSystem extends IteratingSystem {

    private ComponentMapper<Position> pm;
    private ComponentMapper<Velocity> vm;
    private ComponentMapper<PhysicsBody> cm;
    private ComponentMapper<Input> im;

    public MovementSystem() {
        super(Aspect.all(Position.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        Position p = pm.get(entityId);
        Velocity v = vm.get(entityId);

        if (cm.has(entityId)) {
            Body body = world.getSystem(BodyManager.class).getBody(entityId);
            if (im.has(entityId)) { // control physics body by input
                processPhysicsMovement(body, im.get(entityId), p, v);
            } else { // keep image with body for when physics is acting upon it
                p.position.set(body.getPosition());
            }
        } else { // move image directly since there is no physics body
            float d = world.getDelta();
            p.position.add(v.velocity.x * d, v.velocity.y * d);
        }
    }

    private void processPhysicsMovement(Body body, Input ic, Position pc, Velocity vc) {
        body.setLinearVelocity(0, 0);

        // accelerate
        Vector2 force = ic.direction.cpy().nor().scl(ic.speed);
        body.applyLinearImpulse(force, body.getWorldCenter(), true);

        // update entity
        pc.position.set(body.getPosition());
        vc.velocity.set(body.getLinearVelocity());
    }
}
