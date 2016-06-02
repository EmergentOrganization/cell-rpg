package io.github.emergentorganization.cellrpg.scenes.game;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.DebugDisplay;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.scenes.BaseScene;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.DialogDisplay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Game world scene.
 */
public abstract class WorldScene extends BaseScene {
    private final Logger logger = LogManager.getLogger(getClass());
    public DialogDisplay dialogDisplay;
    public DebugDisplay debugDisplay;
    protected World world;
    protected SpriteBatch batch;
    protected EntityFactory entityFactory;

    public WorldScene(final PixelonTransmission pt) {
        super(pt);
        entityFactory = new EntityFactory();
        init(getBaseWorldConfiguration());
        dialogDisplay = new DialogDisplay(stage);
        debugDisplay = new DebugDisplay(world, stage);

    }

    /* ================================================================== */
    /* ====================== INTERFACE METHODS ========================= */
    /* ================================================================== */

    public abstract WorldConfiguration getBaseWorldConfiguration();
    // returns world configuration to start from
    // override this to modify world configuration before calling initArtemis()
    // simplest method is just:
    // return new WorldConfiguration();

    /* ================================================================== */
    /* ================================================================== */
    /* ================================================================== */

    public void init(WorldConfiguration baseConfig) {
        batch = new SpriteBatch();
        logger.warn("WorldScene init!");
        world = WorldFactory.standardGameWorld(pt, batch, stage, entityFactory, baseConfig);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();
        dialogDisplay.update(delta);
        debugDisplay.update(delta);
    }

    @Override
    public void onSceneChange() {
        // Dispose of the Artemis world before scene change --avoids instances of overlap like sound effects or music
        world.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
