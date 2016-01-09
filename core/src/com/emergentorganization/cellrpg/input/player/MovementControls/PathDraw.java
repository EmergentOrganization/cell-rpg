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
    private final boolean DEBUG_MODE = true; //logger.isDebugEnabled();
    // debug-only vars:
    ShapeRenderer shapeRen;

    private final String NAME = "path";
    private final String DESC = "Drag to draw path for player," +
            " click on player to stop moving, tap/click to shoot.";
    public final int PATH_RADIUS_MIN = 1;
    public final int PATH_RADIUS_MAX = 20;
    // max distance to auto-travel away from camera (to keep on-screen in arcade mode)
    private final int MAX_CAMERA_DIST = 30;
    // path-to-player distance close enough to ignore
    final float CLOSE_ENOUGH_TO_PATH = CoordinateRecorder.minPathLen * .1f;
    // max time[ms] before giving up on dest
    final long MAX_DEST_SEEK_TIME = 3000;
    // min distance moved towards dest required else give up
    final float MIN_PROGRESS = CoordinateRecorder.minPathLen * .01f;

    private int pathDrawRadius = 5;  // Radius around player which triggers path redraw
    private boolean autoWalk = false;  // if true player keeps moving in last given direction, else stops

    protected boolean lastFramePressed = false; // If the left mouse button was pressed last frame
    protected long elapsedTime; // Time elapsed since last frame
    protected long lastClick = 0; // Last time the player has clicked the mouse button
    protected Vector3 tmp = new Vector3(); // A temporary vector, so we won't have to create a new every frame
    protected CoordinateRecorder savedPath = new CoordinateRecorder(500); // The coordinate recorder, a utility used for saving the path
    protected boolean clickedPath = false; // Has the player clicked the mouse since he began recording a path
    protected boolean path = false; // Is the player moving along a path
    protected boolean recording = false; // Is the player recording a path
    protected Vector2 dest = null;  // next destination point on path
    protected long destStart = 0;  // time started pursuing current dest
    protected Vector2 lastPos = new Vector2(0,0);  // position from last time

    public PathDraw (World world, ComponentMapper<InputComponent> comp_m, ShapeRenderer renderer){
        super(world, comp_m);
        shapeRen = renderer;
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

        Velocity vel = player.getComponent(Velocity.class);
        Camera cam = world.getSystem(CameraSystem.class).getGameCamera();
        Vector2 mouse = inputUtil.getMousePos(cam);  // this is off-screen
        mouse.scl(EntityFactory.SCALE_BOX_TO_WORLD);  // this is closer, but shifted down-left

//        mouse.add(mouse.x*.1f, mouse.y*.1f);

//        mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());  // this is on screen, but y inverted

        if (DEBUG_MODE) {
//            logger.trace("mouseP:" + mouse);
            inputComponentDebugRender(center, inComp, mouse);
        }

        handlePath(inComp, center, cam);

        clickedPath = false;

        // if the button is pressed right now
        if (framePress) {
//            logger.trace("clicked @ " + mouse);
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
//                    logger.trace("on path");
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
//                logger.trace("start rec new path");
                savedPath.clear();
            }
            recording = true;
            path = true;
        } else if(!savedPath.isEmpty() &&
                savedPath.getCoords().get(savedPath.getCoords().size()-1).dst(mouse) < pathDrawRadius){
//            logger.trace("mouse is close to path end");
            // if mouse is close to current path end
            recording = true;
            path = true;
        } // else mouse not close enough to path areas


    }

    private void nextDest(){
        // movest to next destination
//        logger.trace("new dest");
        dest = savedPath.pop();
        destStart = TimeUtils.millis();
    }

    private void handlePath(
            InputComponent inComp,
            Vector2 pos,
            Camera camera
    ) {
        if (!path || inComp.moveState != MoveState.PATH_FOLLOW)
            return;

        long now = TimeUtils.millis();

        if (dest == null) {
            nextDest();
        }

        if (dest != null) {
            Vector2 dir = dest.cpy().sub(pos).nor();
            inComp.direction.set(dir);
//            inComp.speed = loco.maxSpeed;  speed is constant, set by constructor
            boolean carryOn = true;  // flag to prevent calling nextDest multiple times

            // give up if not making sufficient progress
            if (carryOn && pos.dst2(lastPos) < MIN_PROGRESS){
                logger.trace("nextPos; insufficient progress towards dest");
                nextDest();
                carryOn = false;
            } else if (carryOn){
//                logger.trace("progress made:" + pos.dst2(lastPos) + "m");
                lastPos.set(pos);
            }

            // stop if close enough to path
            if (carryOn && dest.dst(pos) < CLOSE_ENOUGH_TO_PATH){
                logger.trace("nextPos; close enough!");
                nextDest();
                carryOn = false;
            } else if (carryOn){
//                logger.trace(dest.dst(pos) - CLOSE_ENOUGH_TO_PATH + "m til close enough" );
            }

            // stop if taking too long
            if (carryOn && (now - destStart) > MAX_DEST_SEEK_TIME){
                logger.trace("nextPos; taking too long!");
                nextDest();
                carryOn = false;
            } else if (carryOn){
//                logger.trace(MAX_DEST_SEEK_TIME - (now - destStart) + "ms til give up");
            }
        } else {
            if (autoWalk){
                // keep moving unless too far from camera
                if ( pos.dst(camera.position.x, camera.position.y) > MAX_CAMERA_DIST){
                    inComp.stopMoving();
                }
//                logger.trace("autowalk");
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

    public void inputComponentDebugRender(
            Vector2 playerPos,
            InputComponent inComp,
            Vector2 mouse
    ){
        float SCL = EntityFactory.SCALE_WORLD_TO_BOX;
        if (inComp.moveState == MoveState.NOT_MOVING)
            return;

        shapeRen.setColor(Color.MAGENTA);

//        logger.trace("r.PM:" + shapeRen.getProjectionMatrix());
//        logger.trace("r.TM:" + shapeRen.getTransformMatrix());

        // the class-scope player variable is updated only if the mouse is pressed.
//        logger.trace("p:" + playerPos + " -> m:" + mouse);
//        playerPos.scl(1f/.025f);
//        playerPos.y *= -1;
//        mouse.y *= -1;

        if (!path) {
//            logger.trace("line " + playerPos + "->" + mouse);
            // player should be 511, 376
            shapeRen.line(playerPos.x*SCL, playerPos.y*SCL, mouse.x*SCL, mouse.y*SCL);
        } else {
//            logger.trace("drawing path");
            Vector2 prev = null;
            ArrayList<Vector2> v = savedPath.getCoords();
            for (int i = 0; i < v.size(); i++) {
                if (prev == null)
                    prev = playerPos;
                Vector2 cur = v.get(i);
//                logger.trace("path " + prev + "->" + cur);
                shapeRen.line(prev.cpy().scl(SCL), cur.cpy().scl(SCL));
                prev = cur;
            }
        }
    }
}
