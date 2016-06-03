package io.github.emergentorganization.cellrpg.core.systems;

import com.artemis.BaseSystem;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.scenes.game.menu.pause.PauseWindow;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Wire
@Profile(using=EmergentProfiler.class, enabled=true)
public class WindowSystem extends BaseSystem {
    private final Logger logger = LogManager.getLogger(getClass());
    private final PixelonTransmission pt;
    private final Stage stage;
    private final Batch gameBatch;
    private RenderSystem renderSystem;
    private MovementSystem movementSystem;
    private InputSystem inputSystem;
    private boolean isPaused = false;
    private VisWindow pauseWindow;
    private TextureRegion cachedBackground;
    private OrthographicCamera camera;

    public WindowSystem(PixelonTransmission pt, Stage stage, Batch batch) {
        this.pt = pt;
        this.stage = stage;
        this.gameBatch = batch;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.translate(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        camera.update();
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
        boolean wasPaused = isPaused;
        detectPause();
        if (isPaused || wasPaused) { // wasPaused used to fill render gap between paused state and un-paused state
            gameBatch.setProjectionMatrix(camera.combined);
            gameBatch.begin();
            gameBatch.draw(cachedBackground, 0, 0);
            gameBatch.end();

            if (wasPaused && !isPaused) { // if moving to running state. We don't need the cached background anymore
                cachedBackground.getTexture().dispose();
                cachedBackground = null;
            }
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
        cachedBackground = ScreenUtils.getFrameBufferTexture();
        enableSystems(false);
        pauseWindow.setPosition((stage.getWidth() / 2f) - (pauseWindow.getWidth() / 2f),
                (stage.getHeight() / 2f) - (pauseWindow.getHeight() / 2f));
        stage.addActor(pauseWindow.fadeIn());
    }

    public void onResume() {
        isPaused = false;
        enableSystems(true);
        pauseWindow.fadeOut();
    }

    private void enableSystems(boolean paused) {
        for (BaseSystem system : world.getSystems()) {
            if (!system.equals(this)) {
                system.setEnabled(paused);
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }
}
