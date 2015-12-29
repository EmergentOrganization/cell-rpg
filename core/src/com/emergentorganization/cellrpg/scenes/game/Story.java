package com.emergentorganization.cellrpg.scenes.game;

import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.WorldFactory;
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
}