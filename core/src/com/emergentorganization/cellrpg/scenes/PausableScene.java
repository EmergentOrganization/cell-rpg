package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Created by BrianErikson on 7/10/2015.
 */
public class PausableScene extends Scene {
    private boolean paused = false;
    private TextureRegion frameBufferTexture;

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.P) && Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (paused)
                setPaused(false);
            else
                setPaused(true);
        }

        if (!paused)
            super.render(delta);
        else {
            SpriteBatch batch = getSpriteBatch();
            batch.setProjectionMatrix(getUiStage().getCamera().combined);
            batch.begin();
            batch.draw(frameBufferTexture, 0, 0);
            batch.end();
            getUiStage().act();
            drawUI();
        }
    }

    public void setPaused(boolean paused) {
        this.paused = paused;

        if (paused)
            frameBufferTexture = ScreenUtils.getFrameBufferTexture(); // cache frame to prevent double-buffer flickering
    }
}
