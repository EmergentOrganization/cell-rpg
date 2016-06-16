package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.components.Visual;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Equipment with animation which represents the amount of charge stored in the equipment
 * (eg amount of ammo in weapon, shield state, or amount of fuel in movement type)
 */
public abstract class ChargeAnimatedEquipment extends Equipment {
    private List<String> anim;
    private EntityID entityID;
    private RenderIndex renderIndex;
    private String tag;

    protected void setupAnim(List<String> anim, EntityID entID, String tag, RenderIndex zLevel){
        // MUST be called before super.create() in subclass. TODO: is there a better way to do this? ~7yl4r
        this.anim = anim;
        this.entityID = entID;
        this.renderIndex = zLevel;
        this.tag = tag;
    }

    @Override
    public ChargeAnimatedEquipment create(World world, Vector2 pos, int parentId) {
        super.create(world, pos, parentId);

        ent = new EntityBuilder(world, EntityFactory.object, name, entityID.toString(), pos)
                .tag(tag)
                .addBuilder(new VisualBuilder()
                                .texture(getChargeFrame(maxCharge))
                                .renderIndex(renderIndex)
                )
                .build();

        return this;
    }

    @Override
    public int checkCharge(){
        int res = super.checkCharge();
        try {
            ent.getComponent(Visual.class).setTexture(getChargeFrame());
        } catch(NullPointerException ex){
            logger.error("ChargeAnimatedEquipment has no texture! Cannot animate.", ex);
        }
        return res;
    }

    private String getChargeFrame(){
        return getChargeFrame(charge());
    }

    private String getChargeFrame(int chargeLevel){
        // returns index of animation frame which matches current charge level
        int frame = Math.round((float)chargeLevel / (float)maxCharge * (float)maxFrame());
        logger.trace("charge " + chargeLevel + "/" + maxCharge + " = frame " + frame + "/" + maxFrame());
        return anim.get(frame);
    }

    protected int maxFrame(){
        return anim.size()-1;
    }

    private final Logger logger = LogManager.getLogger(getClass());
}
