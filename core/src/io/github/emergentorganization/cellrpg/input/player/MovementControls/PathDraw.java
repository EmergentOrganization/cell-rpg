package io.github.emergentorganization.cellrpg.input.player.MovementControls;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
import com.kotcrab.vis.ui.widget.*;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.InputComponent;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.components.Velocity;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.input.player.iPlayerCtrl;
import io.github.emergentorganization.cellrpg.input.player.inputUtil;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Controls movement and firing using only mouse and 1 button.
 * Click within radius of player and drag to draw a path. New path overwrites old path.
 * Player keeps moving in last path direction given. Click to shoot.
 * <p/>
 * SETTINGS:
 * * autowalk : if true player keeps moving in last direction until stopped
 * * pathDrawRadius : Radius around player which triggers path redraw
 * <p/>
 * ported by 7yl4r on 2016-01-04 from PathInputMethod (by 7yl4r 2015-09-05)
 * based on OrelBitton's DirectFollowAndPathInputMethod
 */
public class PathDraw extends iPlayerCtrl {
    private final int PATH_RADIUS_MIN = 1;
    private final int PATH_RADIUS_MAX = 50;
    private final int PATH_RADIUS_DEFAULT = 5;
    private final int PATH_RADIUS_DELTA = 1;
    // path-to-player distance close enough to ignore
    private final float CLOSE_ENOUGH_TO_PATH = CoordinateRecorder.minPathLen * .1f;
    // max time[ms] before giving up on dest
    private final long MAX_DEST_SEEK_TIME = 3000;
    // min distance moved towards dest required else give up
    private final float MIN_PROGRESS = CoordinateRecorder.minPathLen * .01f;
    private final String DESC = "Drag to draw path for player," +
            " click on player to stop moving, tap/click to shoot.";
    private boolean lastFramePressed = false; // If the left mouse button was pressed last frame
    private long elapsedTime; // Time elapsed since last frame
    private long lastClick = 0; // Last time the player has clicked the mouse button
    protected Vector3 tmp = new Vector3(); // A temporary vector, so we won't have to create a new every frame
    private final CoordinateRecorder savedPath = new CoordinateRecorder(500); // The coordinate recorder, a utility used for saving the path
    private boolean clickedPath = false; // Has the player clicked the mouse since he began recording a path
    private boolean path = false; // Is the player moving along a path
    private boolean recording = false; // Is the player recording a path
    private Vector2 dest = null;  // next destination point on path
    private long destStart = 0;  // time started pursuing current dest
    private final Vector2 lastPos = new Vector2(0, 0);  // position from last time
    private final Logger logger = LogManager.getLogger(getClass());
    // debug-only vars:
    private final ShapeRenderer shapeRen;

    public PathDraw(World world, ComponentMapper<InputComponent> comp_m, ShapeRenderer renderer) {
        super(world, comp_m);
        shapeRen = renderer;
    }

    public String getName() {
        return "path";
    }

    @Override
    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow) {
        final Preferences prefs = GameSettings.getPreferences();

        int pathDrawRadius = prefs.getInteger(GameSettings.KEY_WEAPON_PATHDRAW_RADIUS, PATH_RADIUS_DEFAULT);
        VisLabel pathDrawRadiusLabel = new VisLabel("path draw start area size: ");
        menuTable.add(pathDrawRadiusLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
        final VisLabel pathDrawRadiusValue = new VisLabel(Integer.toString(pathDrawRadius));
        menuTable.add(pathDrawRadiusValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final VisSlider pathDrawRadiusSlider = new VisSlider(PATH_RADIUS_MIN, PATH_RADIUS_MAX, PATH_RADIUS_DELTA, false);
        pathDrawRadiusSlider.setValue(pathDrawRadius);
        pathDrawRadiusSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        int newVal = (int) pathDrawRadiusSlider.getValue();
                        prefs.putInteger(GameSettings.KEY_WEAPON_PATHDRAW_RADIUS, newVal);
                        pathDrawRadiusValue.setText(Integer.toString(newVal));
                        menuWindow.pack();
                    }
                }
        );
        menuTable.add(pathDrawRadiusSlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        // auto-walk toggle

        final VisTextButton toggleAutoWalk = new VisTextButton(
                "AutoWalk:" + Boolean.toString(prefs.getBoolean(GameSettings.KEY_WEAPON_PATHDRAW_AUTOWALK))
        );
        menuTable.add(toggleAutoWalk).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        toggleAutoWalk.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                boolean newVal = !prefs.getBoolean(GameSettings.KEY_WEAPON_PATHDRAW_AUTOWALK);
                prefs.putBoolean(GameSettings.KEY_WEAPON_PATHDRAW_AUTOWALK, newVal);
                toggleAutoWalk.setText("AutoWalk:" + Boolean.toString(newVal));
                menuWindow.pack();
            }
        });
    }

    @Override
    public void process(Entity player) {
        Preferences prefs = GameSettings.getPreferences();
        int pathDrawRadius = prefs.getInteger(GameSettings.KEY_WEAPON_PATHDRAW_RADIUS);

        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);// || Gdx.input.isTouched();

        long now = TimeUtils.millis();
        elapsedTime = now - lastClick;

        // handle movement along path regardless to mouse click
        InputComponent inComp = player.getComponent(InputComponent.class);
        Position pos = player.getComponent(Position.class);
        Bounds bounds = player.getComponent(Bounds.class);
        Vector2 center = pos.getCenter(bounds, 0);
        center.scl(EntityFactory.SCALE_BOX_TO_WORLD);

        Velocity vel = player.getComponent(Velocity.class);
        Camera cam = world.getSystem(CameraSystem.class).getGameCamera();
        Vector2 mouse = inputUtil.getMousePos(cam);
        mouse.scl(EntityFactory.SCALE_BOX_TO_WORLD);

        boolean DEBUG_MODE = true;
        if (DEBUG_MODE) {
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
            handleDrawing(center, inComp, mouse);
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

    private void handleDrawing(Vector2 playerPos, InputComponent incComp, Vector2 mouse) {
        Preferences prefs = GameSettings.getPreferences();
        int pathDrawRadius = prefs.getInteger(GameSettings.KEY_WEAPON_PATHDRAW_RADIUS);

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
        } else if (!savedPath.isEmpty() &&
                savedPath.getCoords().get(savedPath.getCoords().size() - 1).dst(mouse) < pathDrawRadius) {
//            logger.trace("mouse is close to path end");
            // if mouse is close to current path end
            recording = true;
            path = true;
        } // else mouse not close enough to path areas
    }

    private void nextDest() {
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
        Preferences prefs = GameSettings.getPreferences();

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
            if (carryOn && pos.dst2(lastPos) < MIN_PROGRESS) {
                logger.trace("nextPos; insufficient progress towards dest");
                nextDest();
                carryOn = false;
            } else if (carryOn) {
//                logger.trace("progress made:" + pos.dst2(lastPos) + "m");
                lastPos.set(pos);
            }

            // stop if close enough to path
            if (carryOn && dest.dst(pos) < CLOSE_ENOUGH_TO_PATH) {
                logger.trace("nextPos; close enough!");
                nextDest();
                carryOn = false;
            } else if (carryOn) {
//                logger.trace(dest.dst(pos) - CLOSE_ENOUGH_TO_PATH + "m til close enough" );
            }

            // stop if taking too long
            if (carryOn && (now - destStart) > MAX_DEST_SEEK_TIME) {
                logger.trace("nextPos; taking too long!");
                nextDest();
            } else if (carryOn) {
//                logger.trace(MAX_DEST_SEEK_TIME - (now - destStart) + "ms til give up");
            }
        } else {
            if (prefs.getBoolean(GameSettings.KEY_WEAPON_PATHDRAW_AUTOWALK)) {
//                logger.info("autowalk");
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

    private void inputComponentDebugRender(
            Vector2 playerPos,
            InputComponent inComp,
            Vector2 mouse
    ) {
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
            shapeRen.line(playerPos.x * SCL, playerPos.y * SCL, mouse.x * SCL, mouse.y * SCL);
        } else {
//            logger.trace("drawing path");
            Vector2 prev = null;
            ArrayList<Vector2> v = savedPath.getCoords();
            for (Vector2 aV : v) {
                if (prev == null)
                    prev = playerPos;
//                logger.trace("path " + prev + "->" + cur);
                shapeRen.line(prev.cpy().scl(SCL), aV.cpy().scl(SCL));
                prev = aV;
            }
        }
    }
}
