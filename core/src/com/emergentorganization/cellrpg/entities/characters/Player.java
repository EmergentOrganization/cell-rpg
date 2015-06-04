package com.emergentorganization.cellrpg.entities.characters;

import com.badlogic.gdx.Gdx;
import com.emergentorganization.cellrpg.components.MovementComponent;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    private static final String TEXTURE_FILE_NAME = "light.png";

    public Player(){
        super(TEXTURE_FILE_NAME);

        MovementComponent movementComponent = getMovementComponent();
        movementComponent.setWorldPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
    }
}
