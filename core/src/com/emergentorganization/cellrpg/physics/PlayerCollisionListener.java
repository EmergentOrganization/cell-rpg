package com.emergentorganization.cellrpg.physics;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.MovementComponent;
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

        Vector2 normal = new Vector2((float)penetration.getNormal().x, (float)penetration.getNormal().y);
        float depth = (float) penetration.getDepth();

        if (data1.tag == Tag.PLAYER) {
            handle(data1, normal, depth);
            return false;
        }
        else if (data2.tag == Tag.PLAYER) {
            handle(data2, normal, depth);
            return false;
        }
        return true;
    }

    /**
     * Stops the player from moving through objects
     * @param data the bodies data
     * @param normal the collision normal
     * @param depth the depth of the collision
     */
    private void handle(PhysicsComponent.CellUserData data, Vector2 normal, float depth) {
        MovementComponent mc = data.movementComponent;
        normal.scl(-depth);
        mc.setWorldPosition(mc.getWorldPosition().add(normal));
    }
}
