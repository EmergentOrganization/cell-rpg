package io.github.emergentorganization.cellrpg.core.entityfactory.Entities;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.CAInteraction.CAInteraction;
import io.github.emergentorganization.cellrpg.components.CAInteraction.CAInteractionList;
import io.github.emergentorganization.cellrpg.components.CollideEffect;
import io.github.emergentorganization.cellrpg.components.Health;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.*;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;
import io.github.emergentorganization.emergent2dcore.events.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Bullet entity which is shot from a weapon.
 */
public class Bullet extends EntityCreator {

    private final Logger logger = LogManager.getLogger(getClass());

    public Bullet(World world, Vector2 pos, Vector2 dir){
        final EventManager eventManager = world.getSystem(EventManager.class);
        final TagManager tagManager = world.getSystem(TagManager.class);

        final float speed = 10f;
        ent = new EntityBuilder(world, EntityFactory.bullet, "Bullet", EntityID.BULLET.toString(), pos)
                .addBuilder(new VisualBuilder()
                                .texture(Resources.TEX_BULLET)
                                .renderIndex(RenderIndex.BULLET)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                                .bodyFriction(0.0001f)
                                .bodyRestitution(1.0f)
                                .bullet(true)
                )
                .addBuilder(new HealthBuilder(3))
                .addBuilder(new CollideEffectBuilder()
                                .collideDamage(1)
                                .collideSelfDamage(1)
                )
                .addBuilder(new LifecycleBuilder(20f))
                .velocity(speed, dir)
                .build();

        // add cellular automata grid interactions
        Entity vyroidLayer = tagManager.getEntity(CALayer.VYROIDS.getTag());
        Entity geneticLayer = tagManager.getEntity(CALayer.VYROIDS_GENETIC.getTag());
        Entity energyLayer = tagManager.getEntity(CALayer.ENERGY.getTag());
        CAInteractionList interactList = ent.getComponent(CAInteractionList.class);
        interactList
                .addInteraction(
                        vyroidLayer.getId(),
                        new CAInteraction()
                                .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(9, 9), energyLayer.getId())
                                .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(6, 6), vyroidLayer.getId())
                                        // constant visual effect
                                .addCollisionImpactStamp(0, CGoLShapeConsts.SQUARE(
                                        1,
                                        1,
                                        DecayCellRenderer.getMaxOfColorGroup(DecayCellRenderer.colorGroupKeys.WHITE)
                                ), energyLayer.getId())
                                .addEventTrigger(1, GameEvent.VYROID_KILL_STD)
                )
                .addInteraction(
                        geneticLayer.getId(),
                        new CAInteraction()
                                .addCollisionImpactStamp(1, CGoLShapeConsts.BOOM(9, 9), energyLayer.getId())
                                .addCollisionImpactStamp(1, CGoLShapeConsts.EMPTY(3, 3), geneticLayer.getId())
                                .addEventTrigger(1, GameEvent.VYROID_KILL_GENETIC)
                )
                .setColliderRadius(2)
        ;

        // health down on CA collisions
        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                try {
                    if (event.entityId == ent.getId()) {
                        switch (event.event) {
                            case VYROID_KILL_GENETIC:
                            case VYROID_KILL_STD:
                                ent.getComponent(Health.class).health
                                        -= ent.getComponent(CollideEffect.class).selfDamage;
                                break;
                        }
                    }
                } catch (NullPointerException ex) {
                    logger.debug("bullet.getComponent returned null. Bullet likely deleted before event trigger.");
                }
            }
        });
    }
}
