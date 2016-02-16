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

public class SingleShapeWarpRegion implements iRegion {
    private final Logger logger = LogManager.getLogger(getClass());

    public WorldScene scene;
    public long maxLength;  // max time before switching to next region
    public static final long NEVER_EXPIRE = -1;  // set maxLength to this for infinite region lifespan
    private long enterTime;  // time region is entered
    public int[][] shape;
    public float spawnFreq;
    private CALayer[] spawnLayers;

    public SingleShapeWarpRegion(WorldScene parentScene, final long expiresIn, final int[][] shape,
                                 final float spawnFreq, CALayer[] spawnLayers) {
        super();
        scene = parentScene;
        maxLength = expiresIn;
        this.shape = shape;
        this.spawnFreq = spawnFreq;
        this.spawnLayers = spawnLayers;
    }

    public iRegion getNextRegion(World world) {
        if (timeExpired()) {
            logger.info("leaving SingleShapeWarpRegion");
            return RegionBuildTool.getNextRegion(this);
        } else {
            return null;
        }
    }

    public void loadRegion(World world) {
    }

    public void enterRegion(World world) {
        logger.info("entering SingleShapeWarpRegion");
        enterTime = System.currentTimeMillis();
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        setCAEdgeSpawns(tagMan);
        setupSpontGen(player);
    }

    private void setupSpontGen(Entity target){
        // setup power-up spawns around given target entity
        logger.trace("new spongen f="+spawnFreq);
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

    private boolean timeExpired(){
        // return true if time is up
        // NOTE: could shorten length here based on player score or other criterion
        long length = maxLength;
        long timeSpent = System.currentTimeMillis() - enterTime;
//        logger.trace("time left in region: " + (length-timeSpent));
        return timeSpent > length;
    }
}
