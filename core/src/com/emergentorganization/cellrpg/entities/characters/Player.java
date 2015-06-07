package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;
import com.emergentorganization.cellrpg.components.player.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.BodyLoader;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String ID = "char-player/0";
    private static Integer HEIGHT = 40;
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
        addComponent(new PlayerInputComponent(camera));
        PhysicsComponent phys = new PhysicsComponent(getScene().getWorld(), BodyLoader.fetch().generateBody(ID, HEIGHT), Tag.PLAYER);
        phys.enableDebugRenderer(true);
        addComponent(phys);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        camera.position.set(moveComponent.getWorldPosition(), 0);
        camera.update();
    }


}
