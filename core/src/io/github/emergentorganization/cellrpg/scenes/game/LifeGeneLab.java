package io.github.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.engine.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.WorldFactory;


public class LifeGeneLab extends WorldScene {

    public LifeGeneLab(PixelonTransmission pt) {
        super(pt);
        WorldFactory.setupStandardEventSystem(world, pt);

        // setup map
        Vector2 pos = new Vector2(0, 0);
        int playerID = entityFactory.createPlayer(pos.x, pos.y);
        entityFactory.addCALayers(pos, playerID);  // TODO: this should be somewhere else

        WorldFactory.setupStandardWorldEffects(world);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration() {
        return new WorldConfiguration();
    }
}
