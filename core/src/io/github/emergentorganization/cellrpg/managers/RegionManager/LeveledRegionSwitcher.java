package io.github.emergentorganization.cellrpg.managers.RegionManager;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.scenes.game.regions.WarpInEventRegion;
import io.github.emergentorganization.cellrpg.scenes.game.regions.iRegion;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.systems.SpawningSystem;
import io.github.emergentorganization.cellrpg.tools.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Leveled region switcher can be used at the Scene level to control switching of regions.
 * Scene initializes the switcher with region sequence to be used,
 * and triggers region switches by calling nextRegion().
 * Switcher will handle loading & prep of new region,
 * move player into the region and dispose of previous region.
 * Intended to enable easy creation of level-based gameflow.
 * Switcher also (optionally) displays level #.
 * <p/>
 * Adapted from LeveledRegionSwitcher (2015-10-12) by 7yl4r on 12/29/2015.
 */
public class LeveledRegionSwitcher extends iRegionManager {
    private final Logger logger = LogManager.getLogger(getClass());
    private int maxTimeInRegion = -1;
    private int regionNumber = -1;

    public iRegion currentRegion;
    private iRegion nextRegion;

    @Wire
    private EntityFactory entityFactory;
    private SpawningSystem spawningSystem;

    public LeveledRegionSwitcher(iRegion startingRegion) {
        nextRegion = startingRegion;
    }


    /**
     * TODO: This doesn't feel right. Need to come up with a different way to instantiate regions while taking advantage
     * of @Wire injections
     * --Brian
     */

    /**
     * Constructor reserved for WarpInEventRegions
     * @param maxTimeInRegion Maximum time in region
     * @param regionNumber The WarpInEvent region number?
     */
    public LeveledRegionSwitcher(int maxTimeInRegion, int regionNumber) {
        this.maxTimeInRegion = maxTimeInRegion;
        this.regionNumber = regionNumber;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (maxTimeInRegion != -1) { // super sketchy
            nextRegion = new WarpInEventRegion(entityFactory, maxTimeInRegion, regionNumber, spawningSystem);
        }
    }

    @Override
    public void processSystem() {
        if (currentRegion == null) {  // game just started
            // add the static background:
            TagManager tagMan = world.getSystem(TagManager.class);
            Entity player = tagMan.getEntity(Tags.PLAYER);
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

            tagMan.getEntity(CALayer.VYROIDS.getTag()).getComponent(CAGridComponents.class).edgeSpawner
                    = CAEdgeSpawnType.RANDOM_SPARSE;

            tagMan.getEntity(CALayer.VYROIDS_GENETIC.getTag()).getComponent(CAGridComponents.class).edgeSpawner
                    = CAEdgeSpawnType.RANDOM_SPARSE;
        }

        // check if should move to next region
        if (nextRegion != null) {
            nextRegion.loadRegion(world);  // or instantiate region here?

//            currentRegion.leave();
//            currentRegion.destroy();
            currentRegion = nextRegion;
            nextRegion = null;
            currentRegion.enterRegion(world);

        } else {
            nextRegion = currentRegion.getNextRegion(world);
        }
    }
}
