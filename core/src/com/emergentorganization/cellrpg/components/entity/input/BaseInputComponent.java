package com.emergentorganization.cellrpg.components.entity.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.CoordinateRecorder;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class BaseInputComponent {

    private final Logger logger = LogManager.getLogger(getClass());

    public int WALK_TIME = 300; // Time for mouse to be held for the player to begin walking.
    public final int WALK_TIME_MIN = 100;
    public final int WALK_TIME_MAX = 3000;
    public float FREE_MOVEMENT = 200 * Scene.scale;  // The mouse distance from the player to allow free movement
    public final float FREE_MOVEMENT_MIN = 10;
    public final float FREE_MOVEMENT_MAX = 1000;

    private Vector3 tmp = new Vector3(); // A temporary vector, so we won't have to create a new every frame

    private boolean lastFramePressed = false; // If the left mouse button was pressed last frame
    private long elapsedTime; // Time elapsed since last frame
    private long lastClick = 0; // Last time the player has clicked the mouse button

    private CoordinateRecorder cr = new CoordinateRecorder(500); // The coordinate recorder, a utility used for saving the path
    private boolean path = false; // Is the player moving along a path
    private boolean clickedPath = false; // Has the player clicked the mouse since he began recording a path
    private boolean recording = false; // Is the player recording a path

    private Vector3 mouse; // The mouse coordinates for the current frame (if mouse is pressed)
    private Vector2 player; // The player coordinates for the current frame (if mouse is pressed)
    private Vector2 dest = null;

    private PlayerInputComponent parentInputComp;

    public BaseInputComponent(PlayerInputComponent parent) {
        parentInputComp = parent;
    }

    public String getName(){
        // returns human-readable name string
        return "base input component";
    }

    public void addInputConfigButtons(VisTable menuTable, final VisWindow menuWindow){
        // mouse:
        VisLabel walkDelayLabel = new VisLabel("walk delay");
        menuTable.add(walkDelayLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
        final VisLabel walkDelayValue = new VisLabel(Integer.toString(WALK_TIME));
        menuTable.add(walkDelayValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final VisSlider walkDelaySlider = new VisSlider(WALK_TIME_MIN, WALK_TIME_MAX, 1, false);
        walkDelaySlider.setValue(WALK_TIME);
        walkDelaySlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        WALK_TIME = (int) walkDelaySlider.getValue();
                        walkDelayValue.setText(Integer.toString(WALK_TIME));
                        menuWindow.pack();
                    }
                }
        );
        menuTable.add(walkDelaySlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        VisLabel freeMoveLabel = new VisLabel("free-move radius");
        menuTable.add(freeMoveLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
        final VisLabel freeMoveValue = new VisLabel(Integer.toString((int) FREE_MOVEMENT));
        menuTable.add(freeMoveValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final VisSlider freeMoveSlider = new VisSlider(FREE_MOVEMENT_MIN, FREE_MOVEMENT_MAX, 1, false);
        freeMoveSlider.setValue(FREE_MOVEMENT);
        freeMoveSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        FREE_MOVEMENT = (int) freeMoveSlider.getValue();
                        freeMoveValue.setText(Integer.toString((int) FREE_MOVEMENT));
                        menuWindow.pack();
                    }
                }
        );
        menuTable.add(freeMoveSlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        /* telepathic (heh)
        VisTextButton teleTest = new VisTextButton("test telepathic control");
                menuTable.add(teleTest).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                final VisLabel testResult = new VisLabel("...");
                menuTable.add(testResult).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                teleTest.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        testResult.setText("RPGScene failed: no telepathic abilities detected.");
                        menuWindow.pack();
                    }
                });
                break;
         */
    }

    public void inputComponentUpdate(float deltaTime, Camera camera, Vector3 mousePos, MovementComponent mc) {
        // if left mouse button (touch down) is pressed in the current frame
        boolean framePress = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

        long now = TimeUtils.millis();
        elapsedTime = now - lastClick;

        // handle movement along path regardless to mouse click
        handlePath(mc);

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
        }

        lastFramePressed = framePress;
    }

    private void handleMovement(Vector3 mousePos, MovementComponent mc) {
        if (elapsedTime < WALK_TIME)
            return;

        boolean dst = mouse.dst(player.x, player.y, 0) < FREE_MOVEMENT;

        // check if the mouse button is not very far away from the player
        if (!path && dst || path && clickedPath && dst) {
            // if it isn't, use player to mouse direct movement
            recording = false;
            path = false;
            dest = null;
            mc.setMoveState(MovementComponent.MoveState.MOUSE_FOLLOW);
            if (!cr.isEmpty()) {
                cr.clear();
            }

            Vector2 dir = new Vector2(mousePos.x, mousePos.y).sub(mc.getWorldPosition()).nor();
            Vector2 travelDst = dir.scl(mc.getSpeed());
            mc.setVelocity(travelDst);
        } else { // if it is, start recording a path
            recording = true;
            path = true;
            mc.setMoveState(MovementComponent.MoveState.PATH_FOLLOW);
        }
    }

    private void handlePath(MovementComponent mc) {
        if (!path || mc.getMoveState() != MovementComponent.MoveState.PATH_FOLLOW)
            return;

        if (dest == null || dest.dst(mc.getWorldPosition()) <= 1f)
            dest = cr.getFirst();

        if (dest != null) {
            Vector2 dir = dest.cpy().sub(mc.getWorldPosition()).nor();
            Vector2 travelDst = dir.scl(mc.getSpeed());
            mc.setVelocity(travelDst);
        }
        else {
            mc.stopMoving();
        }
    }

    private void handleRecording() {
        if (!recording)
            return;

        cr.record(mouse.x, mouse.y);
    }

    private void handleShooting() {
        if (elapsedTime > WALK_TIME)
            return;

        parentInputComp.shootTo(mouse.x, mouse.y);
    }

    public void inputComponentDebugRender(ShapeRenderer renderer, MovementComponent mc) {
        if (mc.getMoveState() == MovementComponent.MoveState.NOT_MOVING)
            return;

        renderer.setColor(Color.MAGENTA);

        // the class-scope player variable is updated only if the mouse is pressed.
        Vector2 player = mc.getLocalPosition();

        if (!path) {
            renderer.line(player.x, player.y, mouse.x, mouse.y);
        } else {
            Vector2 prev = null;
            ArrayList<Vector2> v = cr.getCoords();
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

    public void skipDest(MovementComponent mc) {
        dest = cr.getFirst();
        if (dest == null)
            mc.stopMoving();
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

    public CoordinateRecorder getCoordinateRecorder() {
        return this.cr;
    }

}
