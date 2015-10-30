package com.emergentorganization.cellrpg.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.scenes.game.menu.pause.PauseWindow;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by brian on 10/30/15.
 */
@Wire
public class WindowSystem extends BaseSystem {
    private final Stage stage;
    private RenderSystem renderSystem;
    private PhysicsRenderSystem physicsRenderSystem;
    private MovementSystem movementSystem;
    private InputSystem inputSystem;
    private boolean isPaused = false;
    private VisWindow pauseWindow;

    public WindowSystem(Stage stage) {
        this.stage = stage;
        this.pauseWindow = new PauseWindow(stage);
    }

    @Override
    protected void begin() {
        stage.act();
    }

    @Override
    protected void processSystem() {
        detectPause();
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
        enableSystems(false);
        pauseWindow.setPosition((stage.getWidth() / 2f) - (pauseWindow.getWidth() / 2f),
                                (stage.getHeight() / 2f) - (pauseWindow.getHeight() / 2f));
        stage.addActor(pauseWindow.fadeIn());
    }

    private void onResume() {
        isPaused = false;
        enableSystems(true);
        pauseWindow.fadeOut();
    }

    private void enableSystems(boolean paused) {
        renderSystem.setEnabled(paused);
        physicsRenderSystem.setEnabled(paused);
        movementSystem.setEnabled(paused);
        inputSystem.setEnabled(paused);
    }

    public boolean isPaused() {
        return isPaused;
    }
}
