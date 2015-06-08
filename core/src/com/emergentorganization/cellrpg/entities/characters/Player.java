package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.components.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.BodyLoader;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String ID = "char-player";  // ID for getting spritesheet and collider
    private static final int FRAME_COLS = 10;  // # of cols in spritesheet
    private static final int FRAME_ROWS = 1;  //  # of rows in spritesheet
    private static final float TPF = 0.2f;  // time per frame of animation

    private OrthographicCamera camera;
    private MovementComponent moveComponent;

    public Player(){
        super(ID + ".png", FRAME_COLS, FRAME_ROWS, TPF);

        moveComponent = getMovementComponent();
        moveComponent.setWorldPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

        addComponent(new WeaponComponent());
    }

    @Override
    public void added() {
        super.added();

        camera = getScene().getGameCamera();

        PlayerInputComponent playerInput = new PlayerInputComponent(camera);
        addComponent(playerInput);

        final TextureRegion currentFrame = getGraphicsComponent().getCurrentFrame();
        int scale = Math.max(currentFrame.getTexture().getWidth(), currentFrame.getTexture().getHeight());
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(),
                                BodyLoader.fetch().generateBody(ID, scale), Tag.PLAYER);
        phys.setUserData(new PlayerUserData(moveComponent, playerInput.getCoordinateRecorder()));
        //phys.enableDebugRenderer(true);

        addComponent(phys);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float EDGE_MARGIN = 100;  // min px between player & screen edge
        float MAX_OFFSET = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())/2-EDGE_MARGIN;  // max player-camera dist
        float PROPORTIONAL_GAIN = deltaTime * moveComponent.getSpeed() / MAX_OFFSET;
        Vector2 pos = getMovementComponent().getWorldPosition();
        Vector2 cameraLoc = new Vector2(camera.position.x, camera.position.y);

        Vector2 offset = new Vector2(pos);
        offset.sub(camera.position.x, camera.position.y);

        cameraLoc.add(offset.scl(PROPORTIONAL_GAIN));
        camera.position.set(cameraLoc, 0);
        camera.update();
    }
}
