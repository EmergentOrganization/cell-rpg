package com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.tools.CoordinateRecorder;
import com.kotcrab.vis.ui.widget.*;

import java.util.ArrayList;

/**
 * Controls movement using wasd keys, mouse click fires weapon.
 *
 * Created by 7yl4r 2015-09-13
 */
public class WASDAndClick extends BaseInputMethod {
    private static final String NAME = "wasd+click";
    private static final String DESC = "wasd keys to move, click to shoot.";

    protected Vector3 tmp = new Vector3(); // A temporary vector, so we won't have to create a new every frame

    protected boolean lastFramePressed = false; // If the left mouse button was pressed last frame
    protected long elapsedTime; // Time elapsed since last frame
    protected long lastClick = 0; // Last time the player has clicked the mouse button

    protected Vector2 player; // The player coordinates for the current frame (if mouse is pressed)

    public WASDAndClick(PlayerInputComponent parent) {
        super(parent);
    }

    @Override
    public String getName(){
        return NAME;
    }

    @Override
    public String getDesc(){
        return DESC;
    }

    @Override
    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow) {
        super.addInputConfigButtons(menuTable, menuWindow);
        // options? I can't think of any...
    }

    @Override
    public void inputComponentUpdate(float deltaTime, Camera camera, Vector3 mousePos, MovementComponent mc) {
        // if left mouse button (touch down) is pressed in the current frame
        mc.setMoveState(MovementComponent.MoveState.NOT_MOVING);

        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);// || Gdx.input.isTouched();

        long now = TimeUtils.millis();
        elapsedTime = now - lastClick;

        // handle movement
        handleMovement(mc);

        if (framePress && !lastFramePressed) { // if the button is pressed right now
            lastClick = now;
        } else if (!framePress && lastFramePressed) {  // if the button was just released
            fetchMouseCoords(camera);
            handleShooting();
        }
        lastFramePressed = framePress;
    }

    private void handleMovement(MovementComponent mc){
        float maxSpeed = mc.getSpeed();
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            mc.setVelocity(new Vector2(0, maxSpeed));
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)){
            mc.setVelocity(new Vector2(0, -maxSpeed));
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)){
            mc.setVelocity(new Vector2(-maxSpeed, 0));
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)){
            mc.setVelocity(new Vector2(maxSpeed, 0));
        } else {
            mc.stopMoving();
        }
    }

    private void handleShooting() {
        parentInputComp.shootTo(mouse.x, mouse.y);
    }

    private void fetchMouseCoords(Camera camera) {
        float x = Gdx.input.getX();
        float y = Gdx.input.getY();

        tmp.set(x, y, 0);

        /*
         *  Not necessary, [ float yCoord = Gdx.graphics.getHeight() - y ] does the same. look up the source.
         */
        mouse = camera.unproject(tmp);
    }

    @Override
    public void inputComponentDebugRender(ShapeRenderer renderer, MovementComponent mc){
        super.inputComponentDebugRender(renderer, mc);
    }
}
