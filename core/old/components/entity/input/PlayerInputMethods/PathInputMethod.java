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
 * Controls movement and firing using only mouse and 1 button.
 * Click within radius of player and drag to draw a path. New path overwrites old path.
 * Player keeps moving in last path direction given. Click to shoot.
 *
 * Created by 7yl4r 2015-09-05
 * based on OrelBitton's DirectFollowAndPathInputMethod.
 */
public class PathInputMethod extends BaseInputMethod {

    public PathInputMethod(PlayerInputComponent parent) {
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
    public void inputComponentUpdate(float deltaTime, Camera camera, Vector3 mousePos, MovementComponent mc) {

    }


    private void handleMovement(Vector3 mousePos, MovementComponent mc) {
        mc.setMoveState(MovementComponent.MoveState.PATH_FOLLOW);

        // check if the mouse button is not very far away from the player
        if (mouse.dst(player.x, player.y, 0) < pathDrawRadius) {
            // if it is close and we're not already recording, start recording new path
            if (!savedPath.isEmpty() && !recording) {
                savedPath.clear();
            }
            recording = true;
            path = true;
        } else if(!savedPath.isEmpty() &&
                  savedPath.getCoords().get(savedPath.getCoords().size()-1).dst(mouse.x, mouse.y) < pathDrawRadius){
            // if mouse is close to current path end
                recording = true;
                path = true;
        } // else mouse not close enough to path areas


    }

    private void handlePath(MovementComponent mc, Camera camera) {
        if (!path || mc.getMoveState() != MovementComponent.MoveState.PATH_FOLLOW)
            return;

        if (dest == null || dest.dst(mc.getWorldPosition()) <= 1f)
            dest = savedPath.pop();

        if (dest != null) {
            Vector2 dir = dest.cpy().sub(mc.getWorldPosition()).nor();
            Vector2 travelDst = dir.scl(mc.getSpeed());
            mc.setVelocity(travelDst);
        }
        else {
            if (fWalk){
                // keep moving unless too far from camera
                if ( mc.getWorldPosition().dst(camera.position.x, camera.position.y) > MAX_CAMERA_DIST){
                    mc.stopMoving();
                }
            } else {
                mc.stopMoving();
            }
        }
    }

    private void handleRecording() {
        if (!recording)
            return;

        savedPath.record(mouse.x, mouse.y);
    }

    private void handleShooting() {
        if (recording)
            return;
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
        if (mc.getMoveState() == MovementComponent.MoveState.NOT_MOVING)
            return;

        renderer.setColor(Color.MAGENTA);

        // the class-scope player variable is updated only if the mouse is pressed.
        Vector2 player = mc.getLocalPosition();

        super.inputComponentDebugRender(renderer, mc);
        if (!path) {
            renderer.line(player.x, player.y, mouse.x, mouse.y);
        } else {
            Vector2 prev = null;
            ArrayList<Vector2> v = savedPath.getCoords();
            for (int i = 0; i < v.size(); i++) {
                if (prev == null)
                    prev = player;
                Vector2 cur = v.get(i);

                renderer.line(prev, cur);
                prev = cur;
            }
        }
    }

    public Vector2 getDest() {
        return dest;
    }

    public CoordinateRecorder getCoordinateRecorder() {
        return this.savedPath;
    }

    @Override
    public void skipDest(MovementComponent mc) {
        dest = savedPath.pop();
        if (dest == null)
            mc.stopMoving();
    }
}
