package com.emergentorganization.cellrpg.scenes.game;

import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.WorldFactory;

/**
 * Created by orelb on 10/28/2015.
 */
public class Arcade extends WorldScene {
    public Arcade(PixelonTransmission pt) {
        super(pt);

        WorldFactory.setupStandardEventSystem(world, pt);

        //setup map
        entityFactory.createPlayer(0, 0);

        WorldFactory.setupStandardWorldEffects(world);
    }
}
