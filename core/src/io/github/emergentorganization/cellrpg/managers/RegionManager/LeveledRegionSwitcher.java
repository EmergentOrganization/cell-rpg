package io.github.emergentorganization.cellrpg.managers.RegionManager;

import com.artemis.Entity;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.scenes.game.regions.iRegion;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.tools.Resources;
import io.github.emergentorganization.emergent2dcore.components.Position;
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

    private iRegion currentRegion;
    private iRegion nextRegion;

    public LeveledRegionSwitcher(iRegion startingRegion) {
        nextRegion = startingRegion;
    }

    @Override
    public void processSystem() {
        if (currentRegion == null){  // game just started
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

            tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                    = CAEdgeSpawnType.RANDOM_SPARSE;

            tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
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
