package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.emergentorganization.cellrpg.scenes.submenus.DebugMenu;
import com.kotcrab.vis.ui.widget.*;

/**
 * Created by BrianErikson on 7/10/2015.
 */
public class PausableScene extends Scene {
    private boolean paused = false;
    private TextureRegion frameBufferTexture;
    private VisWindow pauseWindow;
    private DebugMenu debug_menu;

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

        if (paused) {  // if pausing
            frameBufferTexture = ScreenUtils.getFrameBufferTexture(); // cache frame to prevent double-buffer flickering
            initPauseMenu();
        } else {
            // if unpausing
            closePauseMenu();
        }
    }

    private void closePauseMenu() {
        debug_menu.closeSubmenu();
        if (pauseWindow != null)
            pauseWindow.fadeOut();
    }

    private void initPauseMenu(){
        VisTable table = new VisTable();
        pauseWindow = new VisWindow("", false);
        pauseWindow.setFillParent(false);
        pauseWindow.centerWindow();
        pauseWindow.add(table);
        pauseWindow.clearListeners();

        VisTextButton map = new VisTextButton("map");
        table.add(map).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        map.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                System.out.println("opened map setting");
            }
        });

        VisTextButton settings = new VisTextButton("settings");
        table.add(settings).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                System.out.println("opened settings");
            }
        });

        debug_menu = new DebugMenu(table, this);

        table.align(Align.center);

        getUiStage().addActor(pauseWindow);

    }
}
