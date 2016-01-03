package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.WorldConfiguration;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.scenes.game.HUD.DialogDisplay;

/**
 * Game world scene.
 *
 * Created by 7yl4r on 12/29/2015.
 */
public abstract class WorldScene extends BaseScene {
    protected World world;
    protected SpriteBatch batch;
    protected EntityFactory entityFactory;

    public DialogDisplay dialogDisplay;

    public WorldScene(final PixelonTransmission pt) {
        super(pt);
        init(getBaseWorldConfiguration());
        dialogDisplay = new DialogDisplay(stage);
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
        entityFactory = new EntityFactory();
        world = WorldFactory.standardGameWorld(pt, batch, stage, entityFactory, baseConfig);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();
        dialogDisplay.update(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        world.dispose();
        batch.dispose();
    }

    @Override
    protected boolean shouldStash() {
        return false;
    }
}
