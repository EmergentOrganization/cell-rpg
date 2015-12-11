package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.BulletState;
import com.emergentorganization.cellrpg.components.Name;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.Tags;

/**
 * Created by brian on 11/8/15.
 */
@Wire
public class EntityLifecycleSystem extends IteratingSystem {
    private ComponentMapper<Name> nameMapper;
    private ComponentMapper<Position> pm;
    private ComponentMapper<BulletState> bulletStateMapper;

    private TagManager tagManager;

    public EntityLifecycleSystem() {
        super(Aspect.all());
    }

    @Override
    protected void process(int EntityId) {
        Name name = nameMapper.get(EntityId);
        if (name.internalID.equals(EntityID.BULLET.toString())) {
            manageBullet(EntityId);
        }
        // manage other entities with an else-if
    }

    private void manageBullet(int EntityId) {
        Position playerPosComp = pm.get(tagManager.getEntity(Tags.PLAYER));
        if (playerPosComp != null) {
            Vector2 pos = pm.get(EntityId).position;
            Vector2 playerPos = playerPosComp.position;
            BulletState bulletState = bulletStateMapper.get(EntityId);

            if (pos.cpy().sub(playerPos).len() >= EntityFactory.BULLET_MAX_DIST || bulletState.bounces < 0) {
                world.delete(EntityId);
            }
        }
    }
}
