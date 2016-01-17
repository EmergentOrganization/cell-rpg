package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.Entity;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.engine.PixelonTransmission;
import io.github.emergentorganization.engine.components.Position;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import com.emergentorganization.cellrpg.scenes.game.regions.ArcadeRegion1;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;


public class Story extends WorldScene {
    public Story(final PixelonTransmission pt) {
        super(pt);

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
        wc.setSystem(new LeveledRegionSwitcher(new ArcadeRegion1(this)));  // TODO: change this region
        return wc;
    }
}