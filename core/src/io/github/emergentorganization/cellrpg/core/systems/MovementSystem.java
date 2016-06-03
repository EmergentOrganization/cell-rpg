package io.github.emergentorganization.cellrpg.core.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github.emergentorganization.cellrpg.components.*;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import io.github.emergentorganization.cellrpg.core.components.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Wire
@Profile(using=EmergentProfiler.class, enabled=true)
public class MovementSystem extends IteratingSystem {

    private ComponentMapper<Position> posMapper;
    private ComponentMapper<Velocity> velMapper;
    private ComponentMapper<PhysicsBody> physMapper;
    private ComponentMapper<InputComponent> inputMapper;
    private ComponentMapper<Rotation> rotMapper;
    private ComponentMapper<EquipmentList> equipMapper;
    private ComponentMapper<Bounds> boundsMapper;
    private ComponentMapper<Lifecycle> lifeCycleMapper;

    private final Logger logger = LogManager.getLogger(getClass());

    public MovementSystem() {
        super(Aspect.all(Position.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        try {
            Position p = posMapper.get(entityId);
            Rotation r = rotMapper.get(entityId);
            Velocity v = velMapper.get(entityId);

            if (physMapper.has(entityId)) {
                Body body = world.getSystem(PhysicsSystem.class).getBody(entityId);
                if (inputMapper.has(entityId)) { // control physics body by input
                    processPhysicsMovement(body, inputMapper.get(entityId), p, v, r, entityId);
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
                EquipmentList equipmentList = equipMapper.get(entityId);
                equipmentList.moveEquipment(boundsMapper, posMapper);
                equipmentList.rechargeEquipment(); // TODO: move this to EnergySystem
            }
        } catch (NullPointerException ex){
            logger.error("MoveSys error; killing offending entity #"+entityId, ex);
            world.getEntity(entityId).getComponent(Lifecycle.class).kill();
        }
    }

    private void processPhysicsMovement(Body body, InputComponent ic, Position pc, Velocity vc, Rotation rc, int id) {
        if (body == null){
            logger.error("ERR: cannot process movement for ent#" + id + "; body == null");
            lifeCycleMapper.get(id).kill();
            return;
        }
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
