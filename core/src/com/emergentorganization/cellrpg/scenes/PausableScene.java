package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.emergentorganization.cellrpg.components.entity.ShieldComponent;
import com.kotcrab.vis.ui.widget.*;

/**
 * Created by BrianErikson on 7/10/2015.
 */
public class PausableScene extends Scene {
    private boolean paused = false;
    private TextureRegion frameBufferTexture;
    private VisWindow pauseWindow;
    private VisWindow debugWindow;

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

    private void initDebugMenu(){
        VisTable table = new VisTable();
        debugWindow = new VisWindow("", false);
        debugWindow.setFillParent(false);
        debugWindow.centerWindow();
        debugWindow.add(table);
        debugWindow.clearListeners();

        VisTextButton damageMe = new VisTextButton("damageMe");
        table.add(damageMe).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        damageMe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getPlayer().getFirstComponentByType(ShieldComponent.class).damage(26);
            }
        });
        table.align(Align.center);

        VisTextButton regenShield = new VisTextButton("regenShield");
        table.add(regenShield).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        regenShield.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                // TODO: recharge player shield
                getPlayer().getFirstComponentByType(ShieldComponent.class).recharge(26);
            }
        });
        table.align(Align.center);

        VisTextButton back = new VisTextButton("<-back");
        table.add(back).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                closeDebugMenu();
                System.out.println("back out of debug menu");
            }
        });
        table.align(Align.center);

        getUiStage().addActor(debugWindow);
    }

    private void closeDebugMenu() {
        if (debugWindow != null)
            debugWindow.fadeOut();
    }

    private void closePauseMenu() {
        closeDebugMenu();
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

        VisTextButton debug = new VisTextButton("debug");
        table.add(debug).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        debug.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                initDebugMenu();

            }
        });

        table.align(Align.center);

        getUiStage().addActor(pauseWindow);

    }
}
