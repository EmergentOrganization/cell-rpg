package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;

/**
 * Created by tylar on 6/2/15.
 */
public class FollowingCamera extends Entity {
    // camera behavior:
    private static final float EDGE_MARGIN = 10;  // min px between player & screen edge
    private static final float CLOSE_ENOUGH = 4;  // min distance between player & cam we care about (to reduce small-dist jitter & performance++)
    private static final float CAMERA_LEAD = 20;  // dist camera should try to lead player movement

    private OrthographicCamera camera;
    private Entity target;

    public FollowingCamera(Entity targetEntity) {
        super();

        setNewTarget(targetEntity);
    }

    public void setNewTarget(Entity newTarget){
        target = newTarget;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!getScene().isEditor())
            updateCameraPos(deltaTime);
    }

    private void updateCameraPos(float deltaTime){
        Camera camera = getScene().getGameCamera();

        MovementComponent mc = target.getFirstComponentByType(MovementComponent.class);

        float MAX_OFFSET = Math.min(camera.viewportWidth, camera.viewportHeight)/2-EDGE_MARGIN;  // max player-camera dist
        float PROPORTIONAL_GAIN = deltaTime * mc.getSpeed() / MAX_OFFSET;

        // TODO: check target has movement component? or at least throw meaningful error if not...

        Vector2 pos = mc.getWorldPosition();
        Vector2 cameraLoc = new Vector2(camera.position.x, camera.position.y);

        Vector2 offset = new Vector2(pos);
        offset.sub(camera.position.x, camera.position.y);

        offset.add(mc.getVelocity().nor().scl(CAMERA_LEAD));

        if (Math.abs(offset.x) > CLOSE_ENOUGH || Math.abs(offset.y) > CLOSE_ENOUGH) {
            cameraLoc.add(offset.scl(PROPORTIONAL_GAIN));
            camera.position.set(cameraLoc, 0);
            camera.update();
            //System.out.println("new camera pos:" + cameraLoc);
        }
    }
}
