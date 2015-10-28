package com.emergentorganization.cellrpg.scenes;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.artemis.systems.AnimationSystem;
import com.emergentorganization.cellrpg.artemis.systems.InputSystem;
import com.emergentorganization.cellrpg.artemis.systems.MovementSystem;
import com.emergentorganization.cellrpg.artemis.systems.RenderSystem;

/**
 * Created by orelb on 10/28/2015.
 */
public class ArtemisScene extends ScreenAdapter {

    private World world;

    public ArtemisScene(){
        initArtemis();
    }

    private void initArtemis(){
        WorldConfiguration wc = new WorldConfiguration();
        SpriteBatch batch = new SpriteBatch();

        wc.setSystem(new InputSystem());
        wc.setSystem(new MovementSystem());
        wc.setSystem(new AnimationSystem());
        wc.setSystem(new RenderSystem(batch));

        world = new World(wc);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();
    }
}
