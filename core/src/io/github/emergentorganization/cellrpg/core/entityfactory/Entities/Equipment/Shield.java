package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.tools.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 */
public class Shield extends ChargeAnimatedEquipment {
    private final Logger logger = LogManager.getLogger(getClass());

    public Shield setup(String name, String description, int baseEnergy, int energySlots, int shieldStat){
        super.setup(name, description, baseEnergy, energySlots);
        this.shieldStat = shieldStat;
        return this;
    }

    @Override
    public Shield create(World world, Vector2 pos, int parentId) {
        setupAnim(Resources.ANIM_PLAYER_SHIELD, EntityID.PLAYER_SHIELD, Tags.SHEILD, RenderIndex.PLAYER_SHIELD);
        maxCharge = maxFrame();  // number of charges = number of animation frames available
        super.create(world, pos, parentId);
        this.type = EquipmentType.SHIELD;

        setChargeStats(maxCharge, 1, maxCharge);

        final EventManager eventManager = world.getSystem(EventManager.class);
        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_HIT:
                        charge--;
                        if (charge < 0) {
                            charge = 0;
                            eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_SHIELD_DOWN));
                        } else {
                            onChargeChanged();
                        }
                        break;
                    case POWERUP_PLUS:
                        logger.info("shield (" + charge + ") powerup");
                        if (charge < (maxCharge)) {
                            charge++;
                            logger.debug("shield++");
                            onChargeChanged();
                        }
                        break;
                }
            }
        });
        return this;
    }

    @Override
    public void recharge(){
        super.recharge();
        onChargeChanged();

        if (!isPowered()){
            if (charge > 0) {
                charge -= 1;
            }
        }
    }

    public void updatePosition(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper) {
        if (ent.getId() > -1 && parentId > -1) {
            Bounds shieldBounds = boundsMapper.get(ent.getId());
            Bounds ownerBounds = boundsMapper.get(parentId);
            Position parentPos = posMapper.get(parentId);
            posMapper.get(ent.getId())
                    .position.set(parentPos.position)
                    .sub(
                            shieldBounds.width * 0.5f - ownerBounds.width * 0.5f,
                            shieldBounds.height * 0.5f - ownerBounds.height * 0.5f
                    );
        } else {
            logger.error("cannot updatePos of equip#" + ent.getId() + " of ent#" + parentId);
        }
    }
}
