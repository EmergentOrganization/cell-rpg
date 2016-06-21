package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Charge;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
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
public class Shield extends ChargeAnimatedEquipment {
    private final Logger logger = LogManager.getLogger(getClass());

    public Shield setup(String name, String description, int baseEnergy, int energySlots, int shieldStat){
        super.setup(name, description, baseEnergy, energySlots);
        this.shieldStat = shieldStat;
        return this;
    }

    @Override
    public Shield create(World world, Vector2 pos, int parentId) {
        super.create(world, pos, parentId);  // this calls buildEntity()

        setupEvents(world);  // TODO: this should be called in parent Equipment class

        this.type = EquipmentType.SHIELD;
        return this;
    }

    public void setupEvents(World world){
        final EventManager eventManager = world.getSystem(EventManager.class);
        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_HIT:
                        if (addCharge(-1) == -1) {
                            eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_SHIELD_DOWN));
                        }
                        break;
                    case POWERUP_PLUS:
                        logger.info("shield (" + charge() + ") powerup");
                        addCharge(1);
                        logger.debug("shield++");
                        break;
                }
            }
        });
    }

    @Override
    public void buildEntity(){
        ent = new EntityBuilder(world, EntityFactory.object, name, EntityID.PLAYER_SHIELD.toString(), pos)
                .tag(Tags.SHEILD)
                .addBuilder(new VisualBuilder()
                                .texture(Resources.ANIM_PLAYER_SHIELD.get(0))
                                .renderIndex(RenderIndex.PLAYER_SHIELD)
                )
                .build();

        // TODO: do this with ChargeBuilder
        maxCharge = maxFrame();  // number of charges = number of animation frames available
        Charge charge = ent.getComponent(Charge.class);
        charge.set(initCharge);
        charge.recharge_per_s = rechargeRate;
        charge.maxCharge = maxCharge;

    }

    @Override
    public void recharge(){
        super.recharge();

        if (!isPowered()){
            addCharge(-1);
        }
    }
}
