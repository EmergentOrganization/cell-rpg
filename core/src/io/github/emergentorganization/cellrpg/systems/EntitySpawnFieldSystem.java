package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalIteratingSystem;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.tools.ApparitionCreator.ApparitionCreator;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.systems.RenderSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Profile(using=EmergentProfiler.class, enabled=true)
public class EntitySpawnFieldSystem extends IntervalIteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    @Wire
    private io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory ef;

    private ComponentMapper<Position> pos_m;
    private ComponentMapper<Bounds> bounds_m;
    private ComponentMapper<EntitySpawnField> spawnField_m;
    private AssetManager assetManager;
    private RenderSystem renderSystem;

    public EntitySpawnFieldSystem() {
        super(Aspect.all(Position.class, Bounds.class, EntitySpawnField.class), 1);
    }

    public void process(int id) {
        TagManager tagMan = world.getSystem(TagManager.class);
        EntitySpawnField spawnField = spawnField_m.get(id);
        if (spawnField.readyForSpawn()) {
            spawn(id, spawnField, tagMan);
        } else {
            spawnField.tick();
        }
    }

    private void spawn(int id, EntitySpawnField spawnField, TagManager tagMan) {
        // spawns entity in the field.
        Position pos = pos_m.get(id);
        Bounds bound = bounds_m.get(id);
        ApparitionCreator.apparateEntity(assetManager, renderSystem, spawnField, pos, bound, ef);
    }
}
