package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.CollectibleSpawnField;
import io.github.emergentorganization.cellrpg.components.CollideEffect;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Region in which a single entity is periodically warped in.
 */
public class SingleEntityWarpRegion extends TimedRegion{

    public WorldScene scene;
    public EntityID entityID;
    public float spawnFreq;

    private final Logger logger = LogManager.getLogger(getClass());

    public SingleEntityWarpRegion(WorldScene parentScene, final long expiresIn, final float spawnFreq, EntityID entityID) {
        super(expiresIn);
        scene = parentScene;
        this.entityID = entityID;
        this.spawnFreq = spawnFreq;
    }

    public void loadRegion(World world) {
    }

    @Override
    public void enterRegion(World world) {
        super.enterRegion(world);
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        setupEntitySpawner(player);
    }

    private void setupEntitySpawner(Entity player){
        logger.trace("new entity spawn region f="+spawnFreq);
        CollectibleSpawnField field = player.getComponent(CollectibleSpawnField.class);
        field.entityList.clear();
        field.entityList.add(entityID);
        field.frequency = spawnFreq;
    }
}
