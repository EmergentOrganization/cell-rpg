package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.components.Visual;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.tools.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 */
public class Shield extends Equipment {
    private final Logger logger = LogManager.getLogger(getClass());
    public int shieldEntity = -1;
    public int shieldState = 0;

    public Shield(int parentId, String name, String description, int baseEnergy, int energySlots, int shieldStat) {
        super(parentId, name, description, baseEnergy, energySlots);
        this.type = EquipmentType.SHIELD;

        this.shieldStat = shieldStat;
    }

    public void create(World world, Vector2 pos) {
        final int MAX_SHIELD_STATE = Resources.ANIM_PLAYER_SHIELD.size() - 1;
        final Entity shield = new EntityBuilder(world, EntityFactory.object, name, EntityID.PLAYER_SHIELD.toString(), pos)
                .tag("shield")
                .addBuilder(new VisualBuilder()
                        .texture(Resources.ANIM_PLAYER_SHIELD.get(MAX_SHIELD_STATE))
                        .renderIndex(RenderIndex.PLAYER_SHIELD)
                )
                .build();

        this.shieldEntity = shield.getId();
        this.shieldState = MAX_SHIELD_STATE;

        final EventManager eventManager = world.getSystem(EventManager.class);
        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_HIT:
                        shieldState--;
                        if (shieldState < 0) {
                            shieldState = 0;
                            eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_SHIELD_DOWN));
                        } else {
                            shield.getComponent(Visual.class).setTexture(Resources.ANIM_PLAYER_SHIELD.get(shieldState));
                        }
                        break;
                    case POWERUP_PLUS:
                        logger.info("shield (" + shieldState + ") powerup");
                        if (shieldState < (MAX_SHIELD_STATE)) {
                            shieldState++;
                            logger.debug("shield++");
                            shield.getComponent(Visual.class).setTexture(Resources.ANIM_PLAYER_SHIELD.get(shieldState));
                        }
                        break;
                }
            }
        });
    }

    public void updatePosition(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper) {
        if (this.shieldEntity >= 0) {
            Bounds shieldBounds = boundsMapper.get(this.shieldEntity);
            Bounds ownerBounds = boundsMapper.get(parentId);
            Position parentPos = posMapper.get(parentId);
            posMapper.get(this.shieldEntity)
                    .position.set(parentPos.position)
                    .sub(
                            shieldBounds.width * 0.5f - ownerBounds.width * 0.5f,
                            shieldBounds.height * 0.5f - ownerBounds.height * 0.5f
                    );
        }
    }
}
