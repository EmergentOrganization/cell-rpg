package com.emergentorganization.cellrpg.components.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.tools.map.Map;
import com.emergentorganization.cellrpg.tools.CoordinateRecorder;

import java.util.ArrayList;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class PlayerInputComponent extends InputComponent {

    private final int WALK_TIME = 300; // Time for mouse press/touch to be held for walking instead of shooting in ms;
    private final float FREE_MOVEMENT = 20f; // mouse distance from the player in pixels that lets you control the player freely

    private Camera camera; // Used to unproject screen coordinates for the mouse
    private Vector3 tmp = new Vector3();

    private boolean lastFramePress = false; // If the left mouse button was pressed last frame

    private long elapsedTime, lastPress = 0; // Last press time in milliseconds

    private boolean path = false, firstClick = false;
    private final float pathDrawingDelay = 500;
    private CoordinateRecorder cr = new CoordinateRecorder(pathDrawingDelay);

    // mouse/touch coords for the current frame
    private Vector3 mouse;
    // player coords for the current frame
    private Vector2 player;

    public PlayerInputComponent(Camera camera) {
        type = ComponentType.PLAYER_INPUT;
        this.camera = camera;
    }

    @Override
    public boolean hasWeapon() {
        return true;
    }

    @Override
    public void added() {
        super.added();
    }

    @Override
    public void update(float deltaTime) {
        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        long now = TimeUtils.millis();
        elapsedTime = now - lastPress;

        // move through path
        handlePath();

        // if the button is pressed right now
        if (framePress) {
            if (!lastFramePress)
                lastPress = now;

            // get mouse and player locations
            fetchMouseCoords();
            player = mc.getLocalPosition();

            handleMovement();
            handleRecording();
        }

        // if the button was released
        if (!framePress && lastFramePress) {

            if (elapsedTime < WALK_TIME) {
                handleShooting();
            }
        }

        lastFramePress = framePress;
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        if (mc.getDest() == null)
            return;
        renderer.setColor(Color.CYAN);
        if (!path) {
            renderer.line(player.x, player.y, mouse.x, mouse.y);
        } else {
            Vector2 prev = null;
            ArrayList<Vector2> v = cr.getCoords();
            for (int i = 0; i < v.size(); i++) {
                if (prev == null)
                    prev = mc.getLocalPosition();
                Vector2 cur = v.get(i);

                renderer.line(prev, cur);
                prev = cur;
            }
        }
    }

    @Override
    public boolean shouldDebugRender() {
        return true;
    }

    private void handlePath() {
        if(!path || mc.getDest() != null || cr.isEmpty())
            return;

        mc.setStopOnArrival(false);

        Vector2 coord = cr.getFirst();
        mc.setDest(coord);

        if (cr.isEmpty())
            mc.setStopOnArrival(true);
    }

    private void handleRecording() {
        if (!path)
            return;

        cr.record(mouse.x, mouse.y);
    }

    private void handleMovement() {
        if (elapsedTime < WALK_TIME)
            return;

        if (mouse.dst(player.x, player.y, 0) < FREE_MOVEMENT) {
            System.out.println("regular movement");

            mc.setDest(mouse.x, mouse.y);
            mc.setStopOnArrival(true);
        } else {
            path = true;
        }
    }

    private void handleShooting() {
        shootTo(mouse.x, mouse.y);
    }

    private void fetchMouseCoords() {
        float x = Gdx.input.getX();
        float y = Gdx.input.getY();

        tmp.set(x, y, 0);
        mouse = camera.unproject(tmp);
    }

    public CoordinateRecorder getCoordinateRecorder() {
        return cr;
    }

}
