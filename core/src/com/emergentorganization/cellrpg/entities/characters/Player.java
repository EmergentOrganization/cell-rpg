package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;
import com.emergentorganization.cellrpg.components.player.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.BodyLoader;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String ID = "light";
    private OrthographicCamera camera;
    private MovementComponent moveComponent;

    public Player(){
        super(ID + ".png");

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
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(), BodyLoader.fetch().generateBody(ID, texture.getHeight()), Tag.PLAYER);
        phys.enableDebugRenderer(true);
        phys.setUserData(new PlayerUserData(moveComponent, playerInput.getCoordinateRecorder()));
        addComponent(phys);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float MAX_OFFSET = 150f;  // farthest the player should ever be from the camera
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
