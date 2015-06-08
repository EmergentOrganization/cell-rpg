package com.emergentorganization.cellrpg.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.WeaponComponent;
import com.emergentorganization.cellrpg.util.CoordinateRecorder;

import java.util.ArrayList;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends BaseComponent implements InputProcessor{

    // TODO: implement InputComponent to add AI support.

    private final float timeForWalking = 200; // Time for mouse press/touch to be held for walking instead of shooting in ms;

    private MovementComponent mc;
    private WeaponComponent wc;

    private Camera camera; // Used to unproject screen coordinates for the mouse
    private Vector3 tmp = new Vector3();

    private boolean lastFramePress = false; // If the left mouse button was pressed last frame
    private long now, timePassed;
    private long lastPress = 0; // Last press time in milliseconds

    private boolean recording = false;
    private final float pathDrawingDelay = 100;
    private CoordinateRecorder cr = new CoordinateRecorder(pathDrawingDelay);

    public PlayerInputComponent(Camera camera){
        type = ComponentType.PLAYER_INPUT;
        this.camera = camera;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
        wc = (WeaponComponent) getFirstSiblingByType(ComponentType.WEAPON);

        InputMultiplexer input = (InputMultiplexer) Gdx.input.getInputProcessor();
        input.addProcessor(this);
    }

    @Override
    public void update(float deltaTime) {
        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        long now = TimeUtils.millis();
        long timePassed = now - lastPress;

        handleMovement();

        // if the button wasn't pressed last frame
        if(framePress) {
            if(!lastFramePress){
                lastPress = now;

                // if the button was pressed long enough
            } else if(timePassed >= timeForWalking){
                handleMovement();

                // begin recording a path
                recording = true;
            }
        }

        // if the button was released
        if(!framePress && lastFramePress){

            // if we were recording a path, stop recording
            if(recording) {
                recording = false;
            }
            else {
                handleShooting();
            }

            lastPress = now;
        }

        lastFramePress = framePress;
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        renderer.setColor(Color.YELLOW);
        Vector2 prev = null;
        ArrayList<Vector2> v = cr.getCoords();
        for(int i=0; i< v.size(); i++){
            if(prev == null)
                prev = mc.getLocalPosition();
            Vector2 cur = v.get(i);

            renderer.line(prev, cur);
            prev = cur;
        }
    }

    private void handleMovement(){
        // if we are currently drawing a path and the destination is not set
        // I removed the check for the button press because if we were trying to move and draw a path at the same time
        // the player would not move to the next destination.
        if(mc.getDest() == null) {
            if (!cr.isEmpty()) {
                Vector2 dest = cr.getFirst();
                System.out.println("Moving towards " + dest);
                mc.setDest(dest.x, dest.y);

            // This is temporary and can be very problematic if we want to apply forces on the player unrelated to movement
            } else {
                mc.setVelocity(Vector2.Zero);
            }
        }

    }

    private void handleShooting(){
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

    public CoordinateRecorder getCoordinateRecorder() {
        return cr;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        /*if(keycode == Input.Keys.X){
            //cr.clear();
            mc.removeDest();
        }*/

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(recording){
            Vector3 v = getMouseCoords();

            cr.record(v.x, v.y);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean shouldDebugRender() {
        return true;
    }
}
