package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.WorldFactory;

/**
 * Created by brian on 10/30/15.
 */
public class LifeGeneLab extends WorldScene {

    public LifeGeneLab(PixelonTransmission pt) {
        super(pt);
        WorldFactory.setupStandardEventSystem(world, pt);

        // setup map
        entityFactory.createPlayer(0, 0);

        WorldFactory.setupStandardWorldEffects(world);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration(){
        return new WorldConfiguration();
    }
}
