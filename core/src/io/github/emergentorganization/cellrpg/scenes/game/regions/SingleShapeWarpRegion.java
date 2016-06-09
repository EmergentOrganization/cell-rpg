package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldScene;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

/*
* Region in which a single vyroid colony shape is warping in.
 */

public class SingleShapeWarpRegion extends TimedRegion {
    private final Logger logger = LogManager.getLogger(getClass());

    public final WorldScene scene;
    public final int[][] shape;
    private final float spawnFreq;
    private final CALayer[] spawnLayers;

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

    private void setupSpontGen(Entity target) {
        // setup power-up spawns around given target entity
        logger.trace("new spongen region f=" + spawnFreq);
        SpontaneousGenerationList genList = target.getComponent(SpontaneousGenerationList.class);
        genList.clear();
        genList.stampList.add(shape);
        genList.frequency = spawnFreq;
        Collections.addAll(genList.layers, spawnLayers);
    }

    private void setCAEdgeSpawns(TagManager tagMan) {
//        tagMan.getEntity(CALayer.VYROIDS.getTag()).getComponent(CAGridComponents.class).edgeSpawner
//                = CAEdgeSpawnType.EMPTY;
//
//        tagMan.getEntity(CALayer.VYROIDS_GENETIC.getTag()).getComponent(CAGridComponents.class).edgeSpawner
//                = CAEdgeSpawnType.EMPTY;
    }
}
