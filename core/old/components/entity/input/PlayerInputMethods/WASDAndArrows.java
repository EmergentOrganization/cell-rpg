package com.emergentorganization.cellrpg.components.entity.input.PlayerInputMethods;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Controls movement using wasd keys, mouse click fires weapon.
 *
 * Created by 7yl4r 2015-09-13
 */
public class WASDAndArrows extends BaseInputMethod {
    private static final String NAME = "wasd+arrows";
    private static final String DESC = "wasd keys to move, arrow keys to shoot.";

    protected Vector3 tmp = new Vector3(); // A temporary vector, so we won't have to create a new every frame

    protected long elapsedTime; // Time elapsed since last frame

    protected Vector2 player; // The player coordinates for the current frame (if mouse is pressed)

    public WASDAndArrows(PlayerInputComponent parent) {
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
        player = mc.getLocalPosition();

        // handle movement
        handleMovement(mc);

        handleShooting();
    }

    private void handleMovement(MovementComponent mc){
        float maxSpeed = mc.getSpeed();
        Vector2 velocity = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            velocity.add(0, maxSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)){
            velocity.add(0, -maxSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.add(-maxSpeed, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)){
            velocity.add(maxSpeed, 0);
        }

        float len = velocity.len();
        if (len == 0f){
            mc.stopMoving();
        }
        // TODO: Normalize if diagonals so that you don't go faster than maxSpeed
        else {
            mc.setVelocity(velocity);
        }
    }

    private void handleShooting() {
        Vector2 target = new Vector2(player.x, player.y);
        boolean shooting = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            target.add(0, 10);
            shooting = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            target.add(0, -10);
            shooting = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            target.add(-10, 0);
            shooting = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            target.add(10, 0);
            shooting = true;
        }
        if (shooting) {
            parentInputComp.shootTo(target.x, target.y);
        }
    }


    @Override
    public void inputComponentDebugRender(ShapeRenderer renderer, MovementComponent mc){
        super.inputComponentDebugRender(renderer, mc);
    }
}