package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.WeaponComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.Scene;
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

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
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

        final TextureRegion currentFrame = getGraphicsComponent().getCurrentFrame();
        float scale = Math.max(currentFrame.getTexture().getWidth(), currentFrame.getTexture().getHeight()) * Scene.scale;
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                BodyLoader.fetch().generateBody(ID, scale), Tag.PLAYER);

        if (!getScene().isEditor()) {
            camera = getScene().getGameCamera();
            camera.position.set(getMovementComponent().getWorldPosition()
                    .sub(camera.viewportWidth /2f, camera.viewportHeight /2f), 0f);

            PlayerInputComponent playerInput = new PlayerInputComponent(camera);
            addComponent(playerInput);
            phys.setUserData(new PlayerUserData(this, playerInput));
        }
        //phys.enableDebugRenderer(true);

        addComponent(phys);

        //addComponent(new DialogComponent());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!getScene().isEditor())
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
