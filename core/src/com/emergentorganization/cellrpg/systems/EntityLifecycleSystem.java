package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.EntityIDs;
import com.emergentorganization.cellrpg.core.Tags;

/**
 * Created by brian on 11/8/15.
 */
@Wire
public class EntityLifecycleSystem extends IteratingSystem {
    private ComponentMapper<Visual> vm;
    private ComponentMapper<Position> pm;

    private TagManager tagManager;
    private Position playerPosComp;

    public EntityLifecycleSystem() {
        super(Aspect.all());
    }

    @Override
    protected void begin() {
        if (playerPosComp == null)
            playerPosComp = pm.get(tagManager.getEntity(Tags.PLAYER));
    }

    @Override
    protected void process(int entityId) {
        Visual vc = vm.get(entityId);
        if (vc != null) {
            if (vc.id.equals(EntityIDs.BULLET)) {
                manageBullet(entityId);
            }
            // manage other entities with an else-if
        }
    }

    private void manageBullet(int entityId) {
        Vector2 pos = pm.get(entityId).position;
        Vector2 playerPos = playerPosComp.position;

        if (pos.cpy().sub(playerPos).len() >= EntityFactory.BULLET_MAX_DIST) {
            world.delete(entityId);
        }
    }
}
