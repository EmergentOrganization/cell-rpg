package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.components.PhysicsComponent;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;

/**
 * Created by BrianErikson on 6/6/2015.
 */
public class PlayerCollisionListener extends CollisionAdapter {
    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration
            penetration) {
        PhysicsComponent.CellUserData data1 = (PhysicsComponent.CellUserData) body1.getUserData();
        PhysicsComponent.CellUserData data2 = (PhysicsComponent.CellUserData) body2.getUserData();

        // Stop player from moving through objects
        if (data1.tag == Tag.PLAYER) {
            data1.movementComponent.setVelocity(0);
        }
        else if (data2.tag == Tag.PLAYER) {
            data2.movementComponent.setVelocity(0);
        }

        body1.clearForce();
        body1.clearTorque();
        body2.clearForce();
        body2.clearTorque();
        return false;
    }
}
