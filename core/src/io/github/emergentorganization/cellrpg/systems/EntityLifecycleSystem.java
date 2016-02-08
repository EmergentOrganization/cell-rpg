package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Health;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import io.github.emergentorganization.emergent2dcore.components.Lifecycle;
import io.github.emergentorganization.emergent2dcore.components.Name;
import io.github.emergentorganization.emergent2dcore.components.Position;
import io.github.emergentorganization.cellrpg.core.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Wire
@Profile(using=EmergentProfiler.class, enabled=true)
public class EntityLifecycleSystem extends IteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

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
        if (health != null && health.health < 1) {
            logger.info("entity del: out of health");
            world.delete(EntityId);

            // kill if too far away from player (maxDistance < 0 excluded)
        } else if (playerPosComp != null && maxDistance > 0) {
            Vector2 playerPos = playerPosComp.position;  // ideally use center, but this is close enough.
            if (pos.cpy().sub(playerPos).len() > maxDistance) {
                logger.info("entity del: too far from player");
                world.delete(EntityId);
            }
        }
    }
}
