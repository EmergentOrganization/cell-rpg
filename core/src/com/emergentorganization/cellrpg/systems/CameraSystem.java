package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.CameraFollow;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.events.EventListener;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.managers.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by orelb on 10/29/2015.
 */
@Wire
public class CameraSystem extends IteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    private ComponentMapper<Position> pm;
    private ComponentMapper<Bounds> bm;
    private EventManager eventMan;

    private OrthographicCamera gameCamera;
    private boolean shouldFollow = true;

    public CameraSystem() {
        super(Aspect.all(CameraFollow.class, Position.class));
        gameCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameCamera.zoom = EntityFactory.SCALE_WORLD_TO_BOX;
        gameCamera.lookAt(0, 0, 0);
        gameCamera.update();
    }

    @Override
    public void initialize(){
        super.initialize();
        eventMan.addListener(new EventListener() {
            @Override
            public void notify(GameEvent event) {
                switch (event){
                    case PLAYER_HIT:
                        camShake();
                        break;
                }
            }
        });
    }

    private void camShake(){
        logger.info("camShake!");
        final float offset = .5f;  // TODO: randomize directions
        gameCamera.translate(offset, offset);
//        shouldFollow = !shouldFollow;  // this was just temporary to test that it works
    }

    public Camera getGameCamera() {
        return gameCamera;
    }

    public void setCamFollow(boolean enabled) {
        shouldFollow = enabled;
    }

    private void camFollow(int followEntity) {
        if (shouldFollow) {
            // TODO: Need to replace this with 7yl4rs version
            Position pc = pm.get(followEntity);
            Bounds b = bm.get(followEntity);

            gameCamera.position.set(pc.position.x + (b.width / 2f), pc.position.y + (b.height / 2f), 0);
        }
    }

    @Override
    protected void process(int entityId) {
        camFollow(entityId);
    }

    @Override
    protected void end() {
        gameCamera.update();
    }
}
