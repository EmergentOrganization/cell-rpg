package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.PhysicsComponent;
import com.emergentorganization.cellrpg.components.player.PlayerInputComponent;
import com.emergentorganization.cellrpg.physics.Tag;
import com.emergentorganization.cellrpg.tools.BodyLoader;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String ID = "light";

    public Player(){
        super(ID + ".png");

        MovementComponent movementComponent = getMovementComponent();
        movementComponent.setWorldPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
    }

    @Override
    public void added() {
        Camera c = getScene().getGameCamera();
        addComponent(new PlayerInputComponent(c));

        addComponent(new PhysicsComponent(getScene().getWorld(), BodyLoader.fetch().generateBody(ID), Tag.PLAYER));
    }
}
