package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.tools.Config;
import com.emergentorganization.cellrpg.tools.menus.AdjustableSetting;

/**
 * Created by tylar on 6/2/15.
 */
public class FollowingCamera extends Entity {
    // min px between player & screen edge:
    public static AdjustableSetting edgeMargin = new AdjustableSetting("edge margin", 10, 1, 25, 1);

    // dist camera should try to lead player movement:
    public static AdjustableSetting cameraLead = new AdjustableSetting("camera-lead", 20, 1, 50, 1);

    // min distance between player & cam we care about (to reduce small-dist jitter & performance++):
    public static AdjustableSetting closeEnough = new AdjustableSetting("camera-player nearness cutoff", 4, 1, 30, 1);

    private OrthographicCamera camera;
    private Entity target;

    public FollowingCamera(Entity targetEntity) {
        super();

        setNewTarget(targetEntity);
    }

    @Override
    public void added() {
        super.added();

        Preferences prefs = CellRpg.fetch().getConfiguration().getPreferences();
        edgeMargin.setValue(prefs.getFloat(Config.KEY_CAM_EDGE_MARGIN, 10.0f));
        cameraLead.setValue(prefs.getFloat(Config.KEY_CAM_LEAD, 20.0f));
        closeEnough.setValue(prefs.getFloat(Config.KEY_CAM_NEARNESS_CUTOFF, 4.0f));
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
        camera = getScene().getGameCamera();

        MovementComponent mc = target.getFirstComponentByType(MovementComponent.class);

        final float CLOSE_ENOUGH = closeEnough.getValue();
        final float EDGE_MARGIN = edgeMargin.getValue();
        final float CAMERA_LEAD = cameraLead.getValue();

        // max player-camera dist:
        float MAX_OFFSET = Math.min(camera.viewportWidth, camera.viewportHeight)/2-EDGE_MARGIN;
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
