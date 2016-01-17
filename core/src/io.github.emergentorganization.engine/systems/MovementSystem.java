package io.github.emergentorganization.engine.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.engine.components.*;


@Wire
public class MovementSystem extends IteratingSystem {

    private ComponentMapper<Position> posMapper;
    private ComponentMapper<Velocity> velMapper;
    private ComponentMapper<PhysicsBody> physMapper;
    private ComponentMapper<InputComponent> inputMapper;
    private ComponentMapper<Rotation> rotMapper;
    private ComponentMapper<Equipment> equipMapper;
    private ComponentMapper<Bounds> boundsMapper;

    public MovementSystem() {
        super(Aspect.all(Position.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        Position p = posMapper.get(entityId);
        Rotation r = rotMapper.get(entityId);
        Velocity v = velMapper.get(entityId);

        if (physMapper.has(entityId)) {
            Body body = world.getSystem(PhysicsSystem.class).getBody(entityId);
            if (inputMapper.has(entityId)) { // control physics body by input
                processPhysicsMovement(body, inputMapper.get(entityId), p, v, r);
            } else { // keep image with body for when physics is acting upon it
                p.position.set(body.getPosition());
                r.angle = MathUtils.radiansToDegrees * body.getAngle();
                v.velocity.set(body.getLinearVelocity());
            }
        } else { // move image directly since there is no physics body
            float d = world.getDelta();
            p.position.add(v.velocity.x * d, v.velocity.y * d);
        }

        // Keep equipment with entity
        if (equipMapper.has(entityId)) {
            Equipment equipment = equipMapper.get(entityId);
            if (equipment.shieldEntity >= 0) {
                Bounds shieldBounds = boundsMapper.get(equipment.shieldEntity);
                Bounds ownerBounds = boundsMapper.get(entityId);
                posMapper.get(equipment.shieldEntity)
                        .position.set(p.position)
                        .sub(
                                shieldBounds.width * 0.5f - ownerBounds.width * 0.5f,
                                shieldBounds.height * 0.5f - ownerBounds.height * 0.5f
                        );
            }
        }
    }

    private void processPhysicsMovement(Body body, InputComponent ic, Position pc, Velocity vc, Rotation rc) {
        body.setLinearVelocity(0, 0);

        // accelerate
        Vector2 force = ic.direction.cpy().nor().scl(ic.speed);
        body.applyLinearImpulse(force, body.getWorldCenter(), true);

        // update entity
        pc.position.set(body.getPosition());
        vc.velocity.set(body.getLinearVelocity());
        rc.angle = MathUtils.radiansToDegrees * body.getAngle();
    }
}
