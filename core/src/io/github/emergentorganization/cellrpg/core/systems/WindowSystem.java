package io.github.emergentorganization.cellrpg.core.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.scenes.game.menu.pause.PauseWindow;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;


@Wire
@Profile(using=EmergentProfiler.class, enabled=true)
public class WindowSystem extends BaseSystem {
    private final Stage stage;
    private final Batch gameBatch;
    private RenderSystem renderSystem;
    private MovementSystem movementSystem;
    private InputSystem inputSystem;
    private boolean isPaused = false;
    private VisWindow pauseWindow;
    private TextureRegion framebufferTexture;
    private final PixelonTransmission pt;

    public WindowSystem(PixelonTransmission pt, Stage stage, Batch batch) {
        this.pt = pt;
        this.stage = stage;
        this.gameBatch = batch;
    }

    @Override
    protected void initialize() {
        this.pauseWindow = new PauseWindow(pt, stage, world);
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

    public void onPause() {
        isPaused = true;
        this.framebufferTexture = ScreenUtils.getFrameBufferTexture(); // cache frame to prevent double-buffer flickering
        enableSystems(false);
        pauseWindow.setPosition((stage.getWidth() / 2f) - (pauseWindow.getWidth() / 2f),
                (stage.getHeight() / 2f) - (pauseWindow.getHeight() / 2f));
        stage.addActor(pauseWindow.fadeIn());
    }

    public void onResume() {
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
