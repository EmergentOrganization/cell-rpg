package io.github.emergentorganization.cellrpg.core;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.CollideEffect;
import io.github.emergentorganization.cellrpg.components.Health;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Name;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.components.Rotation;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


class PhysicsContactListener implements ContactListener {
    private final Logger logger = LogManager.getLogger(getClass());
    private final EventManager eventManager;
    private final com.artemis.World world;

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
        // collision damage form A to B
        if (entityA.getComponent(CollideEffect.class) != null
                && entityB.getComponent(Health.class) != null) {
            entityB.getComponent(Health.class).health -= entityA.getComponent(CollideEffect.class).damage;
//            logger.trace("puff health=" + entityB.getComponent(Health.class).health);
        }

        // === specific pair effects:
        if (nameA.internalID.equals(EntityID.BULLET.toString())
                && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            Health bulletHealth = entityA.getComponent(Health.class);
            if (bulletHealth.health < bulletHealth.maxHealth) {
                // cannot hit until after a bounce (helps keep player from shooting self in foot as bullet is leaving)
                eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_HIT));
            }
        } else if (nameA.internalID.equals(EntityID.POWERUP_PLUS.toString())
                && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            eventManager.pushEvent(new EntityEvent(entityB.getId(), GameEvent.POWERUP_PLUS));
            try {
                world.deleteEntity(entityA);
            } catch (RuntimeException ex) {
                logger.trace("powerup_plus already deleted");
            }
        } else if (nameA.internalID.equals(EntityID.POWERUP_STAR.toString())
                && nameB.internalID.equals(EntityID.BULLET.toString())) {
            try {
                Vector2 pos = entityA.getComponent(Position.class)
                        .getCenter(entityA.getComponent(Bounds.class), entityA.getComponent(Rotation.class).angle);
                TagManager tagMan = world.getSystem(TagManager.class);
                tagMan.getEntity(CALayer.VYROIDS.getTag()).getComponent(CAGridComponents.class)
                        .stampCenteredAt(CGoLShapeConsts.EMPTY(30, 30), pos);
                tagMan.getEntity(CALayer.VYROIDS_GENETIC.getTag()).getComponent(CAGridComponents.class)
                        .stampCenteredAt(CGoLShapeConsts.EMPTY(10, 10), pos);
                tagMan.getEntity(CALayer.ENERGY.getTag()).getComponent(CAGridComponents.class)
                        .stampCenteredAt(CGoLShapeConsts.BOOM(30, 30), pos);
                world.deleteEntity(entityA);
            } catch (NullPointerException ex) {
                logger.trace("failed star detonate");
                // powerup may have been deleted
                return;
            } catch (RuntimeException ex) {
                logger.trace("failed star detonate");
                // powerup may have been deleted
                return;
            }
        } else if (nameA.internalID.equals(EntityID.POWERUP_STAR.toString())
                && nameB.internalID.equals(EntityID.PLAYER.toString())) {
            eventManager.pushEvent(new EntityEvent(entityB.getId(), GameEvent.POWERUP_STAR));
            try {
                world.deleteEntity(entityA);
            } catch (RuntimeException ex) {
                logger.trace("star powerup already deleted");
            }
        }
    }

    private void handleContact(Entity entity, Name name) {
//        logger.trace("contact " + name.internalID);

        // collide effects on self
        CollideEffect eff = entity.getComponent(CollideEffect.class);
        if (eff != null) {
            // self-damage from collision
            Health health = entity.getComponent(Health.class);
            if (health != null) {
                health.health -= eff.selfDamage;
//                logger.trace("health-damage="+health.health);
            }
        }

        if (name.internalID.equals(EntityID.BULLET.toString())) {  // TODO: add collide-events to CollideEffect?
            eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.COLLISION_BULLET));
        }
    }

    private int getEntityId(Fixture fixture) {
        return (Integer) fixture.getBody().getUserData();
    }
}
