package com.emergentorganization.cellrpg.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.emergentorganization.cellrpg.scenes.SceneManager;
import com.emergentorganization.cellrpg.scenes.game.menu.pause.PauseWindow;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by brian on 10/30/15.
 */
@Wire
public class WindowSystem extends BaseSystem {
    private final Stage stage;
    private final Batch gameBatch;
    private RenderSystem renderSystem;
    private MovementSystem movementSystem;
    private InputSystem inputSystem;
    private boolean isPaused = false;
    private VisWindow pauseWindow;
    private TextureRegion framebufferTexture;

    public WindowSystem(Stage stage, Batch batch, SceneManager sceneManager) {
        this.stage = stage;
        this.pauseWindow = new PauseWindow(stage, sceneManager);
        this.gameBatch = batch;
    }

    @Override
    protected void begin() {
        stage.act();
    }

    @Override
    protected void processSystem() {
        detectPause();
        if (isPaused) {
            gameBatch.begin();
            gameBatch.draw(framebufferTexture, 0, 0); // TODO: frameBuffer not rendering for some reason
            gameBatch.end();
        }
        stage.draw();
    }

    private void detectPause() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused)
                onResume();
            else
                onPause();
        }
    }

    private void onPause() {
        isPaused = true;
        this.framebufferTexture = ScreenUtils.getFrameBufferTexture(); // cache frame to prevent double-buffer flickering
        enableSystems(false);
        pauseWindow.setPosition((stage.getWidth() / 2f) - (pauseWindow.getWidth() / 2f),
                (stage.getHeight() / 2f) - (pauseWindow.getHeight() / 2f));
        stage.addActor(pauseWindow.fadeIn());
    }

    private void onResume() {
        isPaused = false;
        enableSystems(true);
        pauseWindow.fadeOut();
        framebufferTexture.getTexture().dispose();
        framebufferTexture = null;
    }

    private void enableSystems(boolean paused) {
        renderSystem.setEnabled(paused);
        movementSystem.setEnabled(paused);
        inputSystem.setEnabled(paused);
    }

    public boolean isPaused() {
        return isPaused;
    }
}
