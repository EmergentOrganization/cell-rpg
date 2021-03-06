package io.github.emergentorganization.cellrpg.scenes.game;

import com.artemis.Entity;
import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.WorldType;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldConfigAction;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldScene;
import io.github.emergentorganization.cellrpg.systems.SpawningSystem;
import io.github.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.ScoreDisplay;
import io.github.emergentorganization.cellrpg.scenes.game.regions.WarpInEventRegion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Arcade scene with immobile camera and survival gameplay.
 */
public class Arcade extends WorldScene {

    private final ScoreDisplay scoreDisplay;

    public Arcade(PixelonTransmission pt) {
        super(pt, WorldType.ARCADE);

        Logger logger = LogManager.getLogger(getClass());
        logger.info("enter arcade mode");

        WorldFactory.setupStandardEventSystem(world, pt);
        WorldFactory.setupStdHUD(world, stage);

        // setup map
        logger.info("setting up map");
        Vector2 pos = new Vector2(0, 0);
        int playerID = entityFactory.createEntityByID(EntityID.PLAYER, pos, 0);
        entityFactory.addCALayers(pos, playerID);  // TODO: this should be somewhere else

        WorldFactory.setupStandardWorldEffects(world);
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        // HUD tweaks
        logger.info("adding score display");
        scoreDisplay = new ScoreDisplay(world, stage, player.getId());

        logger.info("adding player-centered powerup spawnfield");
        EntitySpawnField spawnField = player.getComponent(EntitySpawnField.class);
        spawnField.entityList.clear();
        spawnField.entityList.add(EntityID.POWERUP_STAR);
        spawnField.entityList.add(EntityID.POWERUP_PLUS);
        spawnField.period = 5000;

        logger.debug("done init arcade world");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        scoreDisplay.updateScore(delta);
    }
}
