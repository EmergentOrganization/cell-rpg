package com.emergentorganization.cellrpg.scenes.game;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.WorldFactory;
import com.emergentorganization.cellrpg.scenes.BaseScene;

/**
 * Created by orelb on 10/28/2015.
 */
public class Arcade extends BaseScene {
    private World world;

    private SpriteBatch batch;

    public Arcade(PixelonTransmission pt) {
        super(pt);
        initArtemis();
    }

    private void initArtemis() {
        batch = new SpriteBatch();

        EntityFactory entityFactory = new EntityFactory();
        world = WorldFactory.standardGameWorld(pt, batch, stage, entityFactory);

        entityFactory.createPlayer(0, 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();

        super.render(delta);
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
