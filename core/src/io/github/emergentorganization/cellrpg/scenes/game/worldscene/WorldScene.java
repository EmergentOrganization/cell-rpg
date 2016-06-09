package io.github.emergentorganization.cellrpg.scenes.game.worldscene;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.WorldType;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.scenes.BaseScene;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.DebugDisplay;
import io.github.emergentorganization.cellrpg.scenes.game.HUD.DialogDisplay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Game world scene.
 */
public abstract class WorldScene extends BaseScene {
    private final Logger logger = LogManager.getLogger(getClass());
    public final DialogDisplay dialogDisplay;
    private final DebugDisplay debugDisplay;
    protected World world;
    private SpriteBatch batch;
    protected final EntityFactory entityFactory;


    protected WorldScene(final PixelonTransmission pt, WorldType worldType) {
        super(pt);
        entityFactory = new EntityFactory();
        batch = new SpriteBatch();
        logger.info("WorldScene init!");
        world = WorldFactory.createWorld(worldType, pt, batch, stage, entityFactory);
        dialogDisplay = new DialogDisplay(stage);
        debugDisplay = new DebugDisplay(world, stage);

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
