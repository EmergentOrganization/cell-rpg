package io.github.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.WorldType;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldScene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class LifeGeneLab extends WorldScene {

    public LifeGeneLab(PixelonTransmission pt) {
        super(pt, WorldType.ARCADE);
        Logger logger = LogManager.getLogger(getClass());
        logger.info("enter lifegene lab");
        WorldFactory.setupStandardEventSystem(world, pt);

        // setup map
        Vector2 pos = new Vector2(0, 0);
        int playerID = entityFactory.createEntityByID(EntityID.PLAYER, pos, 0);
        entityFactory.addCALayers(pos, playerID);  // TODO: this should be somewhere else

        WorldFactory.setupStandardWorldEffects(world);
    }
}
