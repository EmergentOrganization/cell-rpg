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
    private final String NAME = "path";
    private final String DESC = "Drag to draw path for player," +
            " click on player to stop moving, tap/click to shoot.";

    private int pathDrawRadius = 5;  // Radius around player which triggers path redraw
    public final int PATH_RADIUS_MIN = 1;
    public final int PATH_RADIUS_MAX = 20;

    private boolean autoWalk = false;  // if true player keeps moving in last given direction, else stops

    private final int MAX_CAMERA_DIST = 30;  // max distance to auto-travel away from camera (to keep on-screen in arcade mode)

    protected Vector3 tmp = new Vector3(); // A temporary vector, so we won't have to create a new every frame

    protected boolean lastFramePressed = false; // If the left mouse button was pressed last frame
    protected long elapsedTime; // Time elapsed since last frame
    protected long lastClick = 0; // Last time the player has clicked the mouse button

    protected CoordinateRecorder savedPath = new CoordinateRecorder(500); // The coordinate recorder, a utility used for saving the path
    protected boolean clickedPath = false; // Has the player clicked the mouse since he began recording a path
    protected boolean path = false; // Is the player moving along a path
    protected boolean recording = false; // Is the player recording a path

    protected Vector2 player; // The player coordinates for the current frame (if mouse is pressed)
    protected Vector2 dest = null;

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
    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow) {
        super.addInputConfigButtons(menuTable, menuWindow);

        VisLabel pathDrawRadiusLabel = new VisLabel("pathing start area size: ");
        menuTable.add(pathDrawRadiusLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
        final VisLabel pathDrawRadiusValue = new VisLabel(Integer.toString(pathDrawRadius));
        menuTable.add(pathDrawRadiusValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final VisSlider pathDrawRadiusSlider = new VisSlider(PATH_RADIUS_MIN, PATH_RADIUS_MAX, 1, false);
        pathDrawRadiusSlider.setValue(pathDrawRadius);
        pathDrawRadiusSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        pathDrawRadius = (int) pathDrawRadiusSlider.getValue();
                        pathDrawRadiusValue.setText(Integer.toString(pathDrawRadius));
                        menuWindow.pack();
                    }
                }
        );
        menuTable.add(pathDrawRadiusSlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        // auto-walk toggle
        final VisTextButton toggleAutoWalk = new VisTextButton("AutoWalk:"+Boolean.toString(autoWalk));
        menuTable.add(toggleAutoWalk).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        toggleAutoWalk.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                autoWalk = !autoWalk;
                toggleAutoWalk.setText("AutoWalk:"+ Boolean.toString(autoWalk));
                menuWindow.pack();
            }
        });
    }

    @Override
    public void inputComponentUpdate(float deltaTime, Camera camera, Vector3 mousePos, MovementComponent mc) {
        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);// || Gdx.input.isTouched();


        long now = TimeUtils.millis();
        elapsedTime = now - lastClick;

        // handle movement along path regardless to mouse click
        handlePath(mc, camera);

        clickedPath = false;

        // if the button is pressed right now
        if (framePress) {
            if (!lastFramePressed) {
                lastClick = now;

                if (path)
                    clickedPath = framePress;
            }

            // get mouse and player locations
            fetchMouseCoords(camera);
            player = mc.getLocalPosition();

            // handle movement
            handleMovement(mousePos, mc);

            // handle path recording
            handleRecording();
        }

        // if the button was released
        if (!framePress && lastFramePressed) {
            handleShooting();

            recording = false;
            if (mc.getMoveState() == MovementComponent.MoveState.MOUSE_FOLLOW)
                mc.setMoveState(MovementComponent.MoveState.NOT_MOVING);

            if (mouse.dst(player.x, player.y, 0) < pathDrawRadius) {  // if mouse is released close
                if (!savedPath.isEmpty()) {  // if we're on path
                    Vector2 firstPathCoord = savedPath.getCoords().get(0);
                    if (mouse.dst(firstPathCoord.x, firstPathCoord.y, 0) < pathDrawRadius) {
                        // if path started near player too, assume mini-path, player attempting click-to-stop
                        mc.stopMoving();
                    }
                } else {
                    // not on path, player must have clicked to stop
                    mc.stopMoving();
                }
            }
        }
        lastFramePressed = framePress;
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
            if (autoWalk){
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
