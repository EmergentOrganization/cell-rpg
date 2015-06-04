package com.emergentorganization.cellrpg.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.messages.MoveToMessage;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends BaseComponent {
    private MoveToMessage msg = new MoveToMessage();

    @Override
    public void update(float deltaTime) {
        // listen for press events and broadcast move event
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            msg.set(Gdx.input.getX(), Gdx.input.getY());

            broadcast(ComponentType.MOVEMENT, msg);
        }
    }
}
