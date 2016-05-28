package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.tools.Resources;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;
import io.github.emergentorganization.emergent2dcore.components.Visual;
import io.github.emergentorganization.emergent2dcore.events.EventListener;


public class EquipmentList extends Component {
    public int shieldEntity = -1;
    public int shieldState = 0;

    public void addEquipment(Equipment newEquip, World world, Vector2 pos){
        // Shield  TODO: generalize this and move shield stuff into ShiedEquipment class (which extends Equipment)
        final int MAX_SHIELD_STATE = Resources.ANIM_PLAYER_SHIELD.size() - 1;
        final Entity shield = new EntityBuilder(world, EntityFactory.object, "Energy Shield", EntityID.PLAYER_SHIELD.toString(), pos)
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
//                        System.out.println("shield (" + ec.shieldState + ") powerup");
                        if (shieldState < (MAX_SHIELD_STATE)) {
                            shieldState++;
//                            System.out.println("shield++");
                            shield.getComponent(Visual.class).setTexture(Resources.ANIM_PLAYER_SHIELD.get(shieldState));
                        }
                        break;
                }
            }
        });
    }

    // TODO: this would better fit as a method of the Equipment class
    public void moveEquipment(int parentEntityId, ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper){
        if (this.shieldEntity >= 0) {
            Bounds shieldBounds = boundsMapper.get(this.shieldEntity);
            Bounds ownerBounds = boundsMapper.get(parentEntityId);
            Position parentPos = posMapper.get(parentEntityId);
            posMapper.get(this.shieldEntity)
                    .position.set(parentPos.position)
                    .sub(
                            shieldBounds.width * 0.5f - ownerBounds.width * 0.5f,
                            shieldBounds.height * 0.5f - ownerBounds.height * 0.5f
                    );
        }
    }
}
