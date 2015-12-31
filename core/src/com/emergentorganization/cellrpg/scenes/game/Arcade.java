package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import com.emergentorganization.cellrpg.scenes.game.HUD.ScoreDisplay;
import com.emergentorganization.cellrpg.scenes.game.regions.ArcadeRegion1;

import java.util.ArrayList;

/**
 * Arcade scene with immobile camera and survival gameplay.
 *
 * Created by orelb on 10/28/2015.
 */
public class Arcade extends WorldScene {
    ScoreDisplay scoreDisplay;

    public Arcade(PixelonTransmission pt) {
        super(pt);

        WorldFactory.setupStandardEventSystem(world, pt);

        // setup map
        entityFactory.createPlayer(0, 0);

        WorldFactory.setupStandardWorldEffects(world);

        scoreDisplay = new ScoreDisplay(world, stage);
        scoreDisplay.updateScore();  // TODO: do this on score update events from EventManager
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration(){
        WorldConfiguration wc = new WorldConfiguration();
        ArrayList<Class> regionList = new ArrayList<Class>();
        regionList.add(ArcadeRegion1.class);
        wc.setSystem(new LeveledRegionSwitcher(regionList));
        return wc;
    }
}
