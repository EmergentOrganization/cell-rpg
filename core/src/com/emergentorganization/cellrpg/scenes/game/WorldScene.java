package com.emergentorganization.cellrpg.scenes.game;

import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.WorldFactory;

/**
 * Game world scene.
 *
 * Created by 7yl4r on 12/29/2015.
 */
public abstract class WorldScene extends BaseScene {
    protected World world;

    protected SpriteBatch batch;
    protected EntityFactory entityFactory;

    public WorldScene(final PixelonTransmission pt) {
        super(pt);
        initArtemis();
    }

    private void initArtemis() {
        batch = new SpriteBatch();
        entityFactory = new EntityFactory();
        world = WorldFactory.standardGameWorld(pt, batch, stage, entityFactory);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();
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
