package io.github.emergentorganization.cellrpg.scenes.game;

import com.artemis.Entity;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import io.github.emergentorganization.cellrpg.scenes.game.regions.ArcadeRegion;
import io.github.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Story extends WorldScene {

    public Story(final PixelonTransmission pt) {
        super(pt);
        Logger logger = LogManager.getLogger(getClass());
        logger.info("enter main story");

        WorldFactory.setupStandardEventSystem(world, pt);

        // setup map
        MapTools.importMap("OneEachTestMap", entityFactory);// setup map
        Entity player = world.getSystem(TagManager.class).getEntity(Tags.PLAYER);
        Vector2 position = player.getComponent(Position.class).position;
        entityFactory.addCALayers(position, player.getId());  // TODO: this should be somewhere else

        WorldFactory.setupStandardWorldEffects(world);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration() {
        WorldConfiguration wc = new WorldConfiguration();
        wc.setSystem(new LeveledRegionSwitcher(new ArcadeRegion(this)));  // TODO: change this region
        return wc;
    }
}