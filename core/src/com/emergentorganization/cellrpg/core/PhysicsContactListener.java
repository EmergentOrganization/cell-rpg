package com.emergentorganization.cellrpg.core;

import com.artemis.*;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.managers.EventManager;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by brian on 11/21/15.
 */
public class PhysicsContactListener implements ContactListener {
    private final Logger logger = LogManager.getLogger(getClass());
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
        if (nameA.internalID.equals(EntityID.BULLET.toString())
                && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            BulletState bulletState = entityA.getComponent(BulletState.class);
            if (bulletState.bounces < bulletState.starting_bounces){
                // cannot hit until after a bounce (helps keep player from shooting self in foot as bullet is leaving)
                eventManager.pushEvent(GameEvent.PLAYER_HIT);
            }
        } else if (nameA.internalID.equals(EntityID.POWERUP_PLUS.toString())
                && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            eventManager.pushEvent(GameEvent.POWERUP_PLUS);
            try {
                world.deleteEntity(entityA);
            } catch (RuntimeException ex){
                logger.trace("powerup_plus already deleted");
            }
        } else if (nameA.internalID.equals(EntityID.POWERUP_STAR.toString())
                && nameB.internalID.equals(EntityID.BULLET.toString())) {
            try {
                Vector2 pos = entityA.getComponent(Position.class)
                        .getCenter(entityA.getComponent(Bounds.class));
                TagManager tagMan = world.getSystem(TagManager.class);
                tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class)
                        .stampCenteredAt(CGoLShapeConsts.EMPTY(30, 30), pos);
                tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class)
                        .stampCenteredAt(CGoLShapeConsts.EMPTY(10, 10), pos);
                tagMan.getEntity(Tags.CA_ENERGY).getComponent(CAGridComponents.class)
                        .stampCenteredAt(CGoLShapeConsts.BOOM(30, 30), pos);
                world.deleteEntity(entityA);
            } catch (NullPointerException ex) {
                logger.trace("failed star detonate");
                // powerup may have been deleted
                return;
            } catch (RuntimeException ex){
                logger.trace("failed star detonate");
                // powerup may have been deleted
                return;
            }
        } else if (nameA.internalID.equals(EntityID.POWERUP_STAR.toString())
                && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            eventManager.pushEvent(GameEvent.POWERUP_STAR);
            try {
                world.deleteEntity(entityA);
            } catch (RuntimeException ex){
                logger.trace("star powerup already deleted");
            }
        }
    }

    private void handleContact(Entity entity, Name name) {
        logger.trace("contact " + name.internalID);
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
