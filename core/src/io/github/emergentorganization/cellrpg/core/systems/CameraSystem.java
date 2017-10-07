package io.github.emergentorganization.cellrpg.core.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.CameraFollow;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.components.Velocity;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.events.EventListener;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


@Wire
@Profile(using = EmergentProfiler.class, enabled = true)
public class CameraSystem extends IteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    private ComponentMapper<Position> pm;
    private ComponentMapper<Bounds> bm;
    private ComponentMapper<Velocity> velocity_m;
    private EventManager eventMan;

    private final OrthographicCamera gameCamera;
    private Vector2 gameCamDelta = new Vector2();
    private boolean shouldFollow = true;
    private long lastUpdate;
    private final ArrayList<Runnable> tasks = new ArrayList<Runnable>();

    public CameraSystem() {
        super(Aspect.all(CameraFollow.class, Position.class, Bounds.class, Velocity.class));
        gameCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameCamera.zoom = EntityFactory.SCALE_WORLD_TO_BOX;
        gameCamera.lookAt(0, 0, 0);
        gameCamera.update();
    }

    @Override
    public void initialize() {
        super.initialize();
        eventMan.addListener(new EventListener() {
            @Override
            public void notify(EntityEvent event) {
                switch (event.event) {
                    case PLAYER_HIT:
                        camShake(0.5);
                        break;
                }
            }
        });
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Shake the camera during the next CameraSystem processing event via runnable tasks.<br>
     * THREAD-SAFE
     */
    private void camShake(final double magnitude) {
        logger.trace("camShake!");

        synchronized (tasks) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    float x = (float) (magnitude * Math.random());
                    float y = (float) (magnitude * Math.random());
                    gameCamDelta.add(x,y);
                }
            });
        }
    }

    public Camera getGameCamera() {
        return gameCamera;
    }

    public void setCamFollow(boolean enabled) {
        shouldFollow = enabled;
    }

    private void camFollow(int followEntity) {
        int camfollowMethod = GameSettings.getPreferences().getInteger(GameSettings.KEY_CAM_FOLLOW_METHOD);
        // TODO: switch-case to choose method
        camFollow_simple(followEntity);
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Stays exactly above the given entity. No frills, max performance.
     * @param followEntity The entity (by Artemis index) to follow
     */
    private void camFollow_simple(int followEntity) {
        Position pc = pm.get(followEntity);
        Bounds b = bm.get(followEntity);
        Velocity velocity = velocity_m.get(followEntity);

        Vector2 pos = pc.getCenter(b, 0);
        gameCamDelta.add(pos.sub(gameCamera.position.x, gameCamera.position.y));
        //gameCamera.position.set(pos, 0);
    }

    /**
     * Follows given entity, attempts to lead the entity in the direction of travel.
     * @param followEntity The entity (by Artemis index) to follow
     */
    private void camFollow_leading(int followEntity) {
        if (shouldFollow) {
            Position pc = pm.get(followEntity);
            Bounds b = bm.get(followEntity);
            Velocity velocity = velocity_m.get(followEntity);

            final long deltaTime = System.currentTimeMillis() - lastUpdate;

            // get camera settings:
            Preferences prefs = GameSettings.getPreferences();
            final float EDGE_MARGIN = prefs.getFloat(GameSettings.KEY_CAM_EDGE_MARGIN, 10)
                    * EntityFactory.SCALE_WORLD_TO_BOX;
            final float CLOSE_ENOUGH = prefs.getFloat(GameSettings.KEY_CAM_NEARNESS_CUTOFF, 4)
                    * EntityFactory.SCALE_WORLD_TO_BOX;
            final float CAMERA_LEAD = prefs.getFloat(GameSettings.KEY_CAM_LEAD, 20)
                    * EntityFactory.SCALE_WORLD_TO_BOX;


            Vector2 pos = pc.getCenter(b, 0);
            Vector2 cameraLoc = new Vector2(gameCamera.position.x, gameCamera.position.y);

            Vector2 offset = new Vector2(pos);
            offset.sub(gameCamera.position.x, gameCamera.position.y);

            offset.add(velocity.velocity.nor().scl(CAMERA_LEAD));

            if (Math.abs(offset.x) > CLOSE_ENOUGH || Math.abs(offset.y) > CLOSE_ENOUGH) {
                // compute camera positioning:
                final float MAX_OFFSET = Math.min(gameCamera.viewportWidth, gameCamera.viewportHeight) / 2 - EDGE_MARGIN;  // max player-camera dist
                final float PROPORTIONAL_GAIN = deltaTime * velocity.velocity.len() / MAX_OFFSET;

                cameraLoc.add(offset.scl(PROPORTIONAL_GAIN));
                gameCamDelta.add(cameraLoc.sub(gameCamera.position.x, gameCamera.position.y));
//                logger.info("new camera pos:" + cameraLoc);
            }
        }
    }

    @Override
    protected void begin() {
        gameCamDelta.set(0,0);
    }

    @Override
    protected void process(int entityId) {
        // Run all tasks --such as translating the camera via camShake since updating camera delta is impossible outside
        // of the process method due to begin() zeroing out the delta before the next frame processing
        synchronized (tasks) {
            for (Runnable task : tasks) {
                task.run();
            }
            tasks.clear();
        }

        camFollow(entityId);
    }

    @Override
    protected void end() {
        if (!gameCamDelta.isZero()) {
            gameCamera.translate(gameCamDelta);
            gameCamera.update();
        }
    }
}
