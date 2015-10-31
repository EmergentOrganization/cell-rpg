package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.components.CameraFollow;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.managers.AssetManager;

/**
 * Created by orelb on 10/29/2015.
 */
@Wire
public class CameraSystem extends IteratingSystem {

    private OrthographicCamera cam;

    private ComponentMapper<Position> pm;
    private ComponentMapper<Visual> vm;

    private AssetManager assetManager;

    public CameraSystem() {
        super(Aspect.all(CameraFollow.class, Position.class));
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = EntityFactory.SCALE_WORLD_TO_BOX;
        cam.lookAt(0, 0, 0);
        cam.update();
    }

    public Camera getCam() {
        return cam;
    }

    private void camFollow(int followEntity) {
        // TODO: Need to replace this with 7yl4rs version
        Position pc = pm.get(followEntity);
        Visual v = vm.get(followEntity);
        TextureRegion r = assetManager.getRegion(v.id);
        if (r != null) {
            cam.position.set(pc.position.x + (r.getRegionWidth() / 2f), pc.position.y + (r.getRegionHeight() / 2f), 0);
        } else
            cam.position.set(pc.position, 0f);
    }

    @Override
    protected void process(int entityId) {
        camFollow(entityId);
    }

    @Override
    protected void end() {
        cam.update();
    }
}
