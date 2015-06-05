package com.emergentorganization.cellrpg.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.messages.MoveToMessage;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends BaseComponent {
    private MoveToMessage msg = new MoveToMessage();

    private Camera camera; //used to unproject screen coordinates for the mouse
    private Vector3 tmp = new Vector3();

    public PlayerInputComponent(Camera camera){
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        // listen for press events and broadcast a movement event
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            tmp.set(x, y, 0);
            Vector3 v = camera.unproject(tmp);

            msg.set(v.x, v.y);

            broadcast(ComponentType.MOVEMENT, msg);
        }
    }
}
