package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
* Region in which a single vyroid colony shape is warping in.
 */

public class SingleShapeWarpRegion extends TimedRegion {
    private final Logger logger = LogManager.getLogger(getClass());

    public WorldScene scene;
    public int[][] shape;
    public float spawnFreq;
    private CALayer[] spawnLayers;

    public SingleShapeWarpRegion(WorldScene parentScene, final long expiresIn, final int[][] shape,
                                 final float spawnFreq, CALayer[] spawnLayers) {
        super(expiresIn);
        scene = parentScene;
        this.shape = shape;
        this.spawnFreq = spawnFreq;
        this.spawnLayers = spawnLayers;
    }

    public void loadRegion(World world) {
    }

    @Override
    public void enterRegion(World world) {
        super.enterRegion(world);
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        setCAEdgeSpawns(tagMan);
        setupSpontGen(player);
    }

    private void setupSpontGen(Entity target){
        // setup power-up spawns around given target entity
        logger.trace("new spongen region f="+spawnFreq);
        SpontaneousGenerationList genList = target.getComponent(SpontaneousGenerationList.class);
        genList.clear();
        genList.stampList.add(shape);
        genList.frequency = spawnFreq;
        for (CALayer spawnLayer : spawnLayers){
            genList.layers.add(spawnLayer);
        }
    }

    private void setCAEdgeSpawns(TagManager tagMan) {
//        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
//                = CAEdgeSpawnType.EMPTY;
//
//        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
//                = CAEdgeSpawnType.EMPTY;
    }
}
