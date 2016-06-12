package io.github.emergentorganization.cellrpg.core.entityfactory.Entities;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.EquipmentList;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Shield;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Weapon;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.*;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.managers.PhysicsSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * player entity representing player avatar
 */
public class Player extends EntityCreator {
    public Player(World world, Vector2 pos) {
        final EventManager eventManager = world.getSystem(EventManager.class);
        final TagManager tagManager = world.getSystem(TagManager.class);

        ent = new EntityBuilder(world, EntityFactory.player, "Player", EntityID.PLAYER.toString(), pos)
                .tag(Tags.PLAYER)
                .addBuilder(new VisualBuilder()
                        .animation(Resources.ANIM_PLAYER, Animation.PlayMode.LOOP_PINGPONG, 0.2f)
                        .renderIndex(RenderIndex.PLAYER)
                )
                .addBuilder(new PhysicsBodyBuilder(world.getSystem(PhysicsSystem.class))
                        .setFixedRotation(true)
                        .bodyFriction(0.3f)
                )
                .addBuilder(new InputBuilder()
                        .speed(2f)
                )
                .addBuilder(new SpontaneousGenerationListBuilder(10))// TODO: not sure what this value should be... could use Bounds?
                .addBuilder(new CollectibleSpawnFieldBuilder(10))// TODO: not sure what this should be either
                //.health(1) // shield takes care of this instead
                .build();

        final EquipmentList ec = ent.getComponent(EquipmentList.class);
        if (!ec.loadEquipment(world, pos, ent.getId())){
            // setup default equipment
            ec.addEquipment(
                    new Shield().setup("Default Shield", "basic starter shield", 1, 3, 1),
                    world, pos, ent.getId()
            );
            ec.addEquipment(
                    new Weapon().setup("Default Laser", "basic starter laser", 2, 5, 1),
                    world, pos, ent.getId()
            );
        }

        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                if (event.entityId == ent.getId()) {
                    switch (event.event) {
                        case POWERUP_STAR:
                            Vector2 cen = ent.getComponent(Position.class).getCenter(ent.getComponent(Bounds.class), 0);
                            Entity vyroidLayer = tagManager.getEntity(CALayer.VYROIDS.getTag());
                            Entity geneticLayer = tagManager.getEntity(CALayer.VYROIDS_GENETIC.getTag());
                            Entity energyLayer = tagManager.getEntity(CALayer.ENERGY.getTag());
                            vyroidLayer.getComponent(CAGridComponents.class).stampCenteredAt(CGoLShapeConsts.EMPTY(210, 210), cen);
                            geneticLayer.getComponent(CAGridComponents.class).stampCenteredAt(CGoLShapeConsts.EMPTY(70, 70), cen);
                            energyLayer.getComponent(CAGridComponents.class).stampCenteredAt(CGoLShapeConsts.BOOM(210, 210), cen);
                            break;
                        case DESTROY:
                            // NOTE: currently this never gets called b/c scene is changed before event callbacks fired
                            logger.info("player destroyed");
                            dispose();
                            break;
                    }
                } else {

                }
            }
        });
    }

    public void dispose(){
        logger.debug("disposing player");
        ent.getComponent(EquipmentList.class).saveEquipment();
    }

    private final Logger logger = LogManager.getLogger(getClass());
}
