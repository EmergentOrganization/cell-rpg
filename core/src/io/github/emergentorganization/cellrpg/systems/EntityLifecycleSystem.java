package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Health;
import io.github.emergentorganization.cellrpg.core.ParticleEff;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import io.github.emergentorganization.emergent2dcore.components.Lifecycle;
import io.github.emergentorganization.emergent2dcore.components.Name;
import io.github.emergentorganization.emergent2dcore.components.Position;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.emergent2dcore.systems.RenderSystem;
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
    private AssetManager assetManager;
    private RenderSystem renderSystem;

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

        if (life_m.get(EntityId).manualKill){
            logger.debug("manual kill entity #" + EntityId);
            killEntity(EntityId);
            return;

        // kill if out of health
        }else if (health != null && health.health < 1) {
            logger.debug("entity del: out of health");
            killEntity(EntityId, true);
            return;

            // kill if too far away from player (maxDistance < 0 excluded)
        } else if (playerPosComp != null && maxDistance > 0) {
            Vector2 playerPos = playerPosComp.position;  // ideally use center, but this is close enough.
            if (pos.cpy().sub(playerPos).len() > maxDistance) {
                logger.debug("entity del: too far from player");
                killEntity(EntityId);
                return;
            }
        }
    }

    private void killEntity(int EntityId){
        killEntity(EntityId, false);
    }

    private void killEntity(int EntityId, boolean explode){
        if (explode){
            ParticleEffect particleEffect = assetManager.getParticleEffect(ParticleEff.EXPLODE);
            Vector2 pos = pm.get(EntityId).position;
            particleEffect.setPosition(pos.x, pos.y);
            particleEffect.start();
            renderSystem.registerOrphanParticleEffect(particleEffect);
        }
        world.delete(EntityId);
    }
}
