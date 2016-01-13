package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import com.emergentorganization.cellrpg.scenes.game.HUD.ScoreDisplay;
import com.emergentorganization.cellrpg.scenes.game.regions.ArcadeRegion;

/**
 * Arcade scene with immobile camera and survival gameplay.
 */
public class Arcade extends WorldScene {
    ScoreDisplay scoreDisplay;

    public Arcade(PixelonTransmission pt) {
        super(pt);

        WorldFactory.setupStandardEventSystem(world, pt);
        WorldFactory.setupStdHUD(world, stage);

        // setup map
        Vector2 pos = new Vector2(0, 0);
        int playerID = entityFactory.createPlayer(pos.x, pos.y);
        entityFactory.addCALayers(pos, playerID);  // TODO: this should be somewhere else

        WorldFactory.setupStandardWorldEffects(world);
        TagManager tm = world.getSystem(TagManager.class);

        // HUD tweaks
        scoreDisplay = new ScoreDisplay(world, stage, tm.getEntity(Tags.PLAYER).getId());

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        scoreDisplay.updateScore(delta);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration() {
        WorldConfiguration wc = new WorldConfiguration();
        wc.setSystem(new LeveledRegionSwitcher(new ArcadeRegion(this)));
        return wc;
    }
}
