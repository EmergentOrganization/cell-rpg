package io.github.emergentorganization.cellrpg.scenes.game;

import com.artemis.Entity;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.scenes.game.regions.SingleShapeWarpRegion;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;
import io.github.emergentorganization.emergent2dcore.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.ScoreDisplay;
import io.github.emergentorganization.emergent2dcore.components.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Arcade scene with immobile camera and survival gameplay.
 */
public class Arcade extends WorldScene {
    private final Logger logger = LogManager.getLogger(getClass());

    ScoreDisplay scoreDisplay;

    public Arcade(PixelonTransmission pt) {
        super(pt);
        logger.info("enter arcade mode");

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
        wc.setSystem(new LeveledRegionSwitcher(new SingleShapeWarpRegion(
                this, 10*1000, CGoLShapeConsts.BLINKER_H, .5f, CALayer.vyroid_values()
        )));

        return wc;
    }
}
