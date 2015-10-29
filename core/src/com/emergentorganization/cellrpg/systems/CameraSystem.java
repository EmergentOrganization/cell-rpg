package com.emergentorganization.cellrpg.systems;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.managers.AssetManager;

/**
 * Created by orelb on 10/29/2015.
 */
@Wire
public class CameraSystem extends BaseSystem {

    private OrthographicCamera cam;

    private ComponentMapper<Position> pm;
    private ComponentMapper<Visual> vm;

    private AssetManager assetManager;

    private int followEntity = -1;
    private Position followEntityPos;

    public CameraSystem() {
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = EntityFactory.SCALE_WORLD_TO_BOX;
        cam.lookAt(0, 0, 0);
        cam.update();
    }

    public Camera getCam(){
        return cam;
    }

    public void setFollowEntity(int entityId) {
        this.followEntity = entityId;

        followEntityPos = null;
    }

    private void camFollow() {
        if (followEntity == -1)
            return;

        if (followEntityPos == null) {
            followEntityPos = pm.get(followEntity);
        }

        Visual v = vm.get(followEntity);
        TextureRegion r = assetManager.getRegion(v.id);

        cam.position.set(followEntityPos.position.x + (r.getRegionWidth() / 2), followEntityPos.position.y + (r.getRegionHeight() / 2), 0);
    }

    @Override
    protected void processSystem() {
        camFollow();

        cam.update();
    }
}
