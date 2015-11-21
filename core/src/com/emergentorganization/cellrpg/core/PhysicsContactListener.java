package com.emergentorganization.cellrpg.core;

import com.artemis.*;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.*;
import com.emergentorganization.cellrpg.components.BulletState;
import com.emergentorganization.cellrpg.components.Name;
import com.emergentorganization.cellrpg.components.Visual;

/**
 * Created by brian on 11/21/15.
 */
public class PhysicsContactListener implements ContactListener {
    private com.artemis.World world;

    public PhysicsContactListener(World world) {
        this.world = world;
    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {
        Entity entityA = world.getEntity(getEntityId(contact.getFixtureA()));
        Entity entityB = world.getEntity(getEntityId(contact.getFixtureB()));
        handleContact(entityA, entityB);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void handleContact(Entity entityA, Entity entityB) {
        handleContact(entityA);
        handleContact(entityB);

        // Handle specific collisions below this line
    }

    private void handleContact(Entity entity) {
        Name name = entity.getComponent(Name.class);

        if (name.internalID.equals(EntityIDs.BULLET)) {
            handleBulletContact(entity);
        }
    }

    private void handleBulletContact(Entity entity) {
        BulletState bulletState = entity.getComponent(BulletState.class);
        bulletState.bounces--;
    }

    private int getEntityId(Fixture fixture) {
        return (Integer)fixture.getBody().getUserData();
    }
}
