package com.emergentorganization.cellrpg.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends BaseComponent {

    // TODO: implement InputComponent to add AI support.

    private MovementComponent mc;
    private WeaponComponent wc;

    private Camera camera; // Used to unproject screen coordinates for the mouse
    private Vector3 tmp = new Vector3();

    private final float timeForWalking = 200; // Time for mouse press/touch to be held for walking instead of shooting;

    private boolean lastFramePress = false; // If the left mouse button was pressed last frame
    private long lastPress = 0; // Last press time in milliseconds

    public PlayerInputComponent(Camera camera){
        type = ComponentType.PLAYER_INPUT;
        this.camera = camera;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
        wc = (WeaponComponent) getFirstSiblingByType(ComponentType.WEAPON);
    }

    @Override
    public void update(float deltaTime) {
        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        long now = TimeUtils.millis();
        long timePassed = now - lastPress;

        // if the button wasn't pressed last frame
        if(framePress) {
            if(!lastFramePress){
                lastPress = now;
            } else if(timePassed >= timeForWalking){
                handleMovement();
            }
            // if the button was released
        }else if(!framePress && lastFramePress){
            if(timePassed < timeForWalking) {
                handleShooting();
            }
            lastPress = now;
        }

        lastFramePress = framePress;
    }

    private void handleMovement(){
        // System.out.println("Move")
        Vector3 v = getMouseCoords();
        mc.moveTo(v.x, v.y);
    }

    private void handleShooting(){
        // System.out.println("Shoot");
        Vector3 v = getMouseCoords();
        wc.shootTo(v.x, v.y);
    }

    private Vector3 getMouseCoords(){
        float x = Gdx.input.getX();
        float y = Gdx.input.getY();

        tmp.set(x, y, 0);
        Vector3 v = camera.unproject(tmp);

        return v;
    }
}
