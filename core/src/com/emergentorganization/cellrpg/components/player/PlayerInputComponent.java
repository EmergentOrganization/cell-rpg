package com.emergentorganization.cellrpg.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.MovementComponent;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends BaseComponent {

    private MovementComponent mc;

    private Camera camera; // Used to unproject screen coordinates for the mouse
    private Vector3 tmp = new Vector3();

    private final float timeForWalking = 2000; // Time for mouse press/touch to be held for walking instead of shooting;
    private boolean lastFramePress = false; // If the left mouse button was pressed last frame
    private long lastPress = TimeUtils.millis(); // Last press time in milliseconds

    public PlayerInputComponent(Camera camera){
        this.camera = camera;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
    }

    @Override
    public void update(float deltaTime) {
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        // listen for press events and broadcast a movement event
        // if button is pressed in the current frame
        if(framePress) {

            // if button was released
        }else if(!framePress && lastFramePress){

        }

        lastFramePress = framePress;
    }

    private void handleMovement(){
        System.out.println("Move");
    }

    private void handleShooting(){
        System.out.println("Shoot");
    }
}
