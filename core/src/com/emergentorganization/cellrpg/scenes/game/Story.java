package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import com.emergentorganization.cellrpg.scenes.game.regions.ArcadeRegion1;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;

/**
 * Created by brian on 10/30/15.
 */
public class Story extends WorldScene {
    public Story(final PixelonTransmission pt) {
        super(pt);

        WorldFactory.setupStandardEventSystem(world, pt);

        // setup map
        MapTools.importMap("OneEachTestMap", entityFactory);

        WorldFactory.setupStandardWorldEffects(world);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration(){
        WorldConfiguration wc = new WorldConfiguration();
        wc.setSystem(new LeveledRegionSwitcher(new ArcadeRegion1(this)));  // TODO: change this region
        return wc;
    }
}