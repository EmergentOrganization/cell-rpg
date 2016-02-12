package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Align;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.CollectibleSpawnField;
import io.github.emergentorganization.cellrpg.systems.SystemsTestSuite;
import io.github.emergentorganization.emergent2dcore.components.Position;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.SequentialStoryDialogue;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
* Region in which a single vyroid colony shape is warping in.
 */

public class SingleShapeWarpRegion implements iRegion {
    private final Logger logger = LogManager.getLogger(getClass());

    private WorldScene scene;
    private long maxLength;  // max time before switching to next region
    public static final long NEVER_EXPIRE = -1;  // set maxLength to this for infinite region lifespan
    private long enterTime;  // time region is entered
    private int[][] shape;
    private int spawnFreq;
    private CALayer[] spawnLayers;

    public SingleShapeWarpRegion(WorldScene parentScene, final long expiresIn, final int[][] shape,
                                 final int spawnFreq, CALayer[] spawnLayers) {
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
            return new ArcadeRegion2(scene);
        } else {
            return null;
        }
    }

    public void loadRegion(World world) {
    }

    public void enterRegion(World world) {
        logger.info("entering ShingleShapeWarpRegion");
        enterTime = System.currentTimeMillis();
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        setCAEdgeSpawns(tagMan);
        setupSpontGen(player);

        Entity bg = new EntityBuilder(
                world,
                EntityFactory.object,
                "Arcade Background",
                EntityID.BG_ARCADE.toString(),
                player.getComponent(Position.class).position.cpy().sub(2000 * .025f, 2000 * .025f)  // minus 1/2 texture size
        )
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_BG_ARCADE)
                        .renderIndex(RenderIndex.BACKGROUND)
                )
                .build();
    }

    private void setupSpontGen(Entity target){
        // setup power-up spawns around given target entity
        SpontaneousGenerationList genList = target.getComponent(SpontaneousGenerationList.class);
        genList.clear();
        genList.stampList.add(shape);
        genList.frequency = spawnFreq;
        for (CALayer spawnLayer : spawnLayers){
            genList.layers.add(spawnLayer);
        }
    }

    private void setCAEdgeSpawns(TagManager tagMan) {

        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;

        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
    }

    private boolean timeExpired(){
        // return true if time is up
        // NOTE: could shorten length here based on player score or other criterion
        long length = maxLength;
        long timeSpent = System.currentTimeMillis() - enterTime;
        logger.trace("time left in region: " + (length-timeSpent));
        return timeSpent > length;
    }
}
