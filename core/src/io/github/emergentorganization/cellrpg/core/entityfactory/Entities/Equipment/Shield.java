package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Charge;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.ParticleEff;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.components.Visual;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.ChargeBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.ParticleEffectBuilder;
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
        final Charge charge = ent.getComponent(Charge.class);

        eventManager.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_HIT:
                        if (charge.addCharge(-1) == -1) {
                            eventManager.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_SHIELD_DOWN));
                        }
                        break;
                    case POWERUP_PLUS:
                        logger.info("shield (" + charge.get() + ") powerup");
                        charge.addCharge(1);
                        logger.debug("shield++");
                        break;
                }
            }
        });
    }

    @Override
    public void buildEntity(World world, Vector2 pos, int parentId){

        // number of charges = number of animation frames available
        int maxCharge =  Resources.ANIM_PLAYER_SHIELD.size()-1;

        ent = new EntityBuilder(world, EntityFactory.equipment, name, EntityID.PLAYER_SHIELD.toString(), pos)
                .tag(Tags.SHEILD)
                .addBuilder(new VisualBuilder()
                                .animation(
                                        Resources.ANIM_PLAYER_SHIELD,
                                        Animation.PlayMode.NORMAL,
                                        Resources.ANIM_PLAYER_SHIELD.size()  // 1s/frame (but actually set by charge)
                                )
                                .renderIndex(RenderIndex.PLAYER_SHIELD)
                                .animationType(Visual.AnimationType.CHARGE)
                )
                .addBuilder(new ChargeBuilder(maxCharge)
                                .charge(maxCharge)
                                .rechargeRate(1)
                )
                .addBuilder(new ParticleEffectBuilder(ParticleEff.SHIELD))
                .build();
    }

    @Override
    public void recharge(){
        super.recharge();

        if (!isPowered()){
            ent.getComponent(Charge.class).addCharge(-1);
        }
    }
}
