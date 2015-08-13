package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.GridSeedComponent;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.components.entity.WeaponComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.scenes.CALayer;
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

    private MovementComponent moveComponent;

    // map editor camera:
    private OrthographicCamera camera;

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public Player(){
        super(ID + ".png", FRAME_COLS, FRAME_ROWS, TPF);

        moveComponent = getMovementComponent();

        addComponent(new WeaponComponent());
        initCAGrid();
    }

    public Player(Texture texture, Vector2 position) {
        super(texture, FRAME_COLS, FRAME_ROWS, TPF);

        moveComponent = getMovementComponent();
        moveComponent.setWorldPosition(position);

        addComponent(new WeaponComponent());
        initCAGrid();
    }

    private void initCAGrid(){
        int[][] pattern = {
                {1,1,1}
        };
        addComponent(new GridSeedComponent(
                pattern,
                5,
                new Vector2(0,0),  // TODO: place this in center of img
                CALayer.VYROIDS
        ));
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
    }
}
