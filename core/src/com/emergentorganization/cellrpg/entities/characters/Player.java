package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;
import com.emergentorganization.cellrpg.components.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.map.Map;
import com.emergentorganization.cellrpg.tools.physics.BodyLoader;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String ID = "char-player";  // ID for getting spritesheet and collider
    private static final int FRAME_COLS = 10;  // # of cols in spritesheet
    private static final int FRAME_ROWS = 1;  //  # of rows in spritesheet
    private static final float TPF = 0.2f;  // time per frame of animation

    // camera behavior:
    private static final float EDGE_MARGIN = 10;  // min px between player & screen edge
    private static final float CLOSE_ENOUGH = 4;  // min distance between player & cam we care about (to reduce small-dist jitter & performance++)
    private static final float CAMERA_LEAD = 20;  // dist camera should try to lead player movement

    private OrthographicCamera camera;
    private MovementComponent moveComponent;

    public Player(){
        super(ID + ".png", FRAME_COLS, FRAME_ROWS, TPF);

        moveComponent = getMovementComponent();

        addComponent(new WeaponComponent());
    }

    public Player(Texture texture, Vector2 position) {
        super(texture, FRAME_COLS, FRAME_ROWS, TPF);

        moveComponent = getMovementComponent();
        moveComponent.setWorldPosition(position);

        addComponent(new WeaponComponent());
    }

    @Override
    public void added() {
        super.added();

        camera = getScene().getGameCamera();
        camera.position.set(getMovementComponent().getWorldPosition()
                                                    .sub(camera.viewportWidth /2f, camera.viewportHeight /2f), 0f);

        PlayerInputComponent playerInput = new PlayerInputComponent(camera);
        addComponent(playerInput);

        final TextureRegion currentFrame = getGraphicsComponent().getCurrentFrame();
        float scale = Math.max(currentFrame.getTexture().getWidth(), currentFrame.getTexture().getHeight()) * Map.scale;
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.PLAYER);
        phys.setUserData(new PlayerUserData(moveComponent, playerInput.getCoordinateRecorder()));
        //phys.enableDebugRenderer(true);

        addComponent(phys);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateCameraPos(deltaTime);
    }

    private void updateCameraPos(float deltaTime){
        Camera camera = getScene().getGameCamera();
        float MAX_OFFSET = Math.min(camera.viewportWidth, camera.viewportHeight)/2-EDGE_MARGIN;  // max player-camera dist
        float PROPORTIONAL_GAIN = deltaTime * moveComponent.getSpeed() / MAX_OFFSET;
        Vector2 pos = getMovementComponent().getWorldPosition();
        Vector2 cameraLoc = new Vector2(camera.position.x, camera.position.y);

        Vector2 offset = new Vector2(pos);
        offset.sub(camera.position.x, camera.position.y);

        offset.add(moveComponent.getVelocity().nor().scl(CAMERA_LEAD));

        if (Math.abs(offset.x) > CLOSE_ENOUGH || Math.abs(offset.y) > CLOSE_ENOUGH) {
            cameraLoc.add(offset.scl(PROPORTIONAL_GAIN));
            camera.position.set(cameraLoc, 0);
            camera.update();
            //System.out.println("new camera pos:" + cameraLoc);
        }
    }
}
