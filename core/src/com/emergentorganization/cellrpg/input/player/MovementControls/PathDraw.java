package com.emergentorganization.cellrpg.input.player.MovementControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
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
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.input.MoveState;
import com.emergentorganization.cellrpg.input.player.iPlayerCtrl;
import com.emergentorganization.cellrpg.input.player.inputUtil;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import com.kotcrab.vis.ui.widget.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/** Controls movement and firing using only mouse and 1 button.
 * Click within radius of player and drag to draw a path. New path overwrites old path.
 * Player keeps moving in last path direction given. Click to shoot.
 *
 * ported by 7yl4r on 2016-01-04 from PathInputMethod (by 7yl4r 2015-09-05)
 * based on OrelBitton's DirectFollowAndPathInputMethod
 */
public class PathDraw extends iPlayerCtrl {
    Logger logger = LogManager.getLogger(getClass());
    private final boolean DEBUG_MODE = true;
    // debug-only vars:
    ShapeRenderer shapeRen;

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
    protected Vector2 dest = null;  // next destination point on path

    public PathDraw (World world, ComponentMapper<InputComponent> comp_m){
        super(world, comp_m);
        if (DEBUG_MODE){
            shapeRen = new ShapeRenderer();
            shapeRen.setAutoShapeType(true);
        }
    }

    public String getName(){
        return NAME;
    }

    @Override
    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow) {
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
    public void process(Entity player){
        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);// || Gdx.input.isTouched();

        long now = TimeUtils.millis();
        elapsedTime = now - lastClick;

        // handle movement along path regardless to mouse click
        InputComponent inComp = player.getComponent(InputComponent.class);
        Position pos = player.getComponent(Position.class);
        Bounds bounds = player.getComponent(Bounds.class);
        Vector2 center = pos.getCenter(bounds);
        center.scl(EntityFactory.SCALE_BOX_TO_WORLD);

        LocomotionComponent loco = player.getComponent(LocomotionComponent.class);
        Velocity vel = player.getComponent(Velocity.class);
        Camera cam = world.getSystem(CameraSystem.class).getGameCamera();
        Vector2 mouse = inputUtil.getMousePos(cam);  // this is off-screen
        mouse.scl(EntityFactory.SCALE_BOX_TO_WORLD);  // this is closer, but shifted down-left

//        mouse.add(mouse.x*.1f, mouse.y*.1f);

//        mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());  // this is on screen, but y inverted

        if (DEBUG_MODE) {
//            logger.info("mouseP:" + mouse);
            shapeRen.begin();
            inputComponentDebugRender(shapeRen, center, inComp, mouse);
            shapeRen.end();
        }

        handlePath(inComp, center, loco, cam);

        clickedPath = false;

        // if the button is pressed right now
        if (framePress) {
//            logger.info("clicked @ " + mouse);
            if (!lastFramePressed) {
                lastClick = now;

                if (path)
                    clickedPath = framePress;
            }

            // handle movement
            handleMovement(center, inComp, mouse);

            // handle path recording
            handleRecording(mouse);
        }

        // if the button was released
        if (!framePress && lastFramePressed) {
            recording = false;
            if (inComp.moveState == MoveState.MOUSE_FOLLOW)
                inComp.moveState = MoveState.NOT_MOVING;

            if (mouse.dst(center) < pathDrawRadius) {  // if mouse is released close
                if (!savedPath.isEmpty()) {  // if we're on path
//                    logger.info("on path");
                    Vector2 firstPathCoord = savedPath.getCoords().get(0);
                    if (mouse.dst(firstPathCoord.x, firstPathCoord.y) < pathDrawRadius) {
                        // if path started near player too, assume mini-path, player attempting click-to-stop
                        inComp.stopMoving();
                    }
                } else {
                    // not on path, player must have clicked to stop
                    inComp.stopMoving();
                }
            }
        }
        lastFramePressed = framePress;
    }

    private void handleMovement(Vector2 playerPos, InputComponent incComp, Vector2 mouse) {
        incComp.moveState = MoveState.PATH_FOLLOW;

        // check if the mouse button is not very far away from the player
        if (mouse.dst(playerPos) < pathDrawRadius) {
            // if it is close and we're not already recording, start recording new path
            if (!savedPath.isEmpty() && !recording) {
//                logger.info("start rec new path");
                savedPath.clear();
            }
            recording = true;
            path = true;
        } else if(!savedPath.isEmpty() &&
                savedPath.getCoords().get(savedPath.getCoords().size()-1).dst(mouse) < pathDrawRadius){
//            logger.info("mouse is close to path end");
            // if mouse is close to current path end
            recording = true;
            path = true;
        } // else mouse not close enough to path areas


    }

    private void handlePath(
            InputComponent inComp,
            Vector2 pos,
            LocomotionComponent loco,
            Camera camera
    ) {
        final float CLOSE_ENOUGH_TO_PATH = CoordinateRecorder.minPathLen;  // path-to-player distance close enough to ignore
        if (!path || inComp.moveState != MoveState.PATH_FOLLOW)
            return;

        if (dest == null || dest.dst(pos) <= CLOSE_ENOUGH_TO_PATH)
            dest = savedPath.pop();

        if (dest != null) {
            Vector2 dir = dest.cpy().sub(pos).nor();
            Vector2 travelDst = dir.scl(loco.maxSpeed);
            inComp.direction.set(dir);
            inComp.speed = loco.maxSpeed;
        } else {
            if (autoWalk){
                // keep moving unless too far from camera
                if ( pos.dst(camera.position.x, camera.position.y) > MAX_CAMERA_DIST){
                    inComp.stopMoving();
                }
            } else {
                inComp.stopMoving();
            }
        }
    }

    private void handleRecording(Vector2 mousePos) {
        if (!recording)
            return;
        savedPath.record(mousePos.x, mousePos.y);
    }

    public void inputComponentDebugRender(ShapeRenderer renderer, Vector2 playerPos, InputComponent inComp, Vector2 mouse){

        if (inComp.moveState == MoveState.NOT_MOVING)
            return;

        renderer.setColor(Color.MAGENTA);

        // the class-scope player variable is updated only if the mouse is pressed.
//        logger.info("p:" + playerPos + " -> m:" + mouse);
//        playerPos.scl(1f/.025f);
//        playerPos.y *= -1;
//        mouse.y *= -1;

        if (!path) {
//            logger.info("line " + playerPos + "->" + mouse);
            // player should be 511, 376
            renderer.line(playerPos.x, playerPos.y, mouse.x, mouse.y);
        } else {
//            logger.info("drawing path");
            Vector2 prev = null;
            ArrayList<Vector2> v = savedPath.getCoords();
            for (int i = 0; i < v.size(); i++) {
                if (prev == null)
                    prev = playerPos;
                Vector2 cur = v.get(i);
//                logger.info("path " + prev + "->" + cur);
                renderer.line(prev, cur);
                prev = cur;
            }
        }
    }
}
