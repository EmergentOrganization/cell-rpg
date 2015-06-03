package com.emergentorganization.cellrpg.entities.characters;

import com.emergentorganization.cellrpg.components.MovementComponent;

/**
 * Created by tylar on 6/2/15.
 */
public class Player extends Character {
    // TODO: Handle this in the RenderComponent when it's created
    private static final String TEXTURE_FILE_NAME = "light.png";

    public Player(){
        super(TEXTURE_FILE_NAME);

        MovementComponent movementComponent = getMovementComponent();
        movementComponent.setWorldPosition(300,300);
    }
}
