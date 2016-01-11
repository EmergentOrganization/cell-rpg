package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.Health;
import com.emergentorganization.cellrpg.components.Lifecycle;
import com.emergentorganization.cellrpg.components.Name;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.Tags;

/**
 * Created by brian on 11/8/15.
 */
@Wire
public class EntityLifecycleSystem extends IteratingSystem {
    private ComponentMapper<Name> nameMapper;
    private ComponentMapper<Position> pm;
    private ComponentMapper<Health> health_m;
    private ComponentMapper<Lifecycle> life_m;

    private TagManager tagManager;

    public EntityLifecycleSystem() {
        super(Aspect.all());
    }

    @Override
    protected void process(int EntityId) {
        Name name = nameMapper.get(EntityId);
        Health health = health_m.get(EntityId);
        Position playerPosComp = pm.get(tagManager.getEntity(Tags.PLAYER));
        Vector2 pos = pm.get(EntityId).position;
        float maxDistance = life_m.get(EntityId).maxPlayerDist;

        // kill if out of health
        if(health != null && health.health < 0) {
            world.delete(EntityId);

        // kill if too far away from player (maxDistance < 0 excluded)
        } else if (playerPosComp != null && maxDistance > 0) {
            Vector2 playerPos = playerPosComp.position;  // ideally use center, but this is close enough.
            if (pos.cpy().sub(playerPos).len() > maxDistance) {
                world.delete(EntityId);
            }
        }
    }
}
