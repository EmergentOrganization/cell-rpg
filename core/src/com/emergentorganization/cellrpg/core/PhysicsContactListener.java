package com.emergentorganization.cellrpg.core;

import com.artemis.*;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.*;
import com.emergentorganization.cellrpg.components.BulletState;
import com.emergentorganization.cellrpg.components.Name;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.managers.EventManager;

/**
 * Created by brian on 11/21/15.
 */
public class PhysicsContactListener implements ContactListener {
    private final EventManager eventManager;
    private com.artemis.World world;

    public PhysicsContactListener(World world) {
        this.world = world;
        this.eventManager = world.getSystem(EventManager.class);
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
        Name nameA = entityA.getComponent(Name.class);
        Name nameB = entityB.getComponent(Name.class);
        handleContact(entityA, nameA);
        handleContact(entityB, nameB);

        handleContactPair(entityA, nameA, entityB, nameB);
        handleContactPair(entityB, nameB, entityA, nameA);
    }

    private void handleContactPair(Entity entityA, Name nameA, Entity entityB, Name nameB) {
        if (nameA.internalID.equals(EntityID.BULLET.toString()) && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            eventManager.pushEvent(GameEvent.PLAYER_HIT);
        }
    }

    private void handleContact(Entity entity, Name name) {
        if (name.internalID.equals(EntityID.BULLET.toString())) {
            handleBulletContact(entity);
        }
    }

    private void handleBulletContact(Entity entity) {
        BulletState bulletState = entity.getComponent(BulletState.class);
        eventManager.pushEvent(GameEvent.COLLISION_BULLET);
        bulletState.bounces--;
    }

    private int getEntityId(Fixture fixture) {
        return (Integer)fixture.getBody().getUserData();
    }
}
