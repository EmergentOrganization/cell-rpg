package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.artemis.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EditorInputProcessor implements InputProcessor {
    private static final float ZOOM_FACTOR = 0.001f;
    private MapEditor editor;
    public static float HIT_ACCURACY = 5f; // lower the value, the more accurate the hit detection
    private Vector2 dragOffset = new Vector2();

    public EditorInputProcessor(MapEditor editor) {
        this.editor = editor;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (editor.isMapInputEnabled()) {
            Vector2 screenCoords = new Vector2(screenX, screenY);
            if (button == Input.Buttons.LEFT) {
                onLeftClick(screenCoords);
            }
            else if (button == Input.Buttons.RIGHT) {
                onRightClick(screenCoords);
            }
        }

        return false;
    }

    private void onLeftClick(Vector2 screenCoords) {
        if (editor.isMapInputEnabled()) {
            Vector3 uiVec = editor.getUiStage().getCamera().unproject(new Vector3(screenCoords, 0f));
            Vector2 click = new Vector2(uiVec.x, uiVec.y);
            editor.setLastLMBClick(click);

            Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(screenCoords.x, screenCoords.y, 0f));
            Vector3 bl = gameVec.cpy().sub(HIT_ACCURACY, HIT_ACCURACY, 0f);
            Vector3 tr = gameVec.cpy().add(HIT_ACCURACY, HIT_ACCURACY, 0f);
            final ArrayList<Integer> entities = new ArrayList<Integer>();
            editor.getPhysWorld().QueryAABB(new QueryCallback() {
                @Override
                public boolean reportFixture(Fixture fixture) {
                    Body body = fixture.getBody();
                    entities.add((Integer) body.getUserData());

                    return false;
                }
            }, bl.x, bl.y, tr.x, tr.y);

            editor.setMapTarget(null);

            for (Integer entityId : entities) {
                Entity entity = editor.getWorld().getEntity(entityId);
                editor.setMapTarget(entity);
                setDragOffset(screenCoords);
            }
        }
    }

    private void onRightClick(Vector2 screenPos) {
        if (editor.isMapInputEnabled()) {
            Vector3 worldPos = editor.getUiStage().getCamera().unproject(new Vector3(screenPos.x, screenPos.y, 0f));
            editor.setLastRMBClick(new Vector2(worldPos.x, worldPos.y));
            editor.openContextMenu();
        }
    }

    private void setDragOffset(Vector2 mousePos) {
        Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(mousePos.x, mousePos.y, 0f));
        Entity mapTarget = editor.getMapTarget();

        if (mapTarget != null) {
            Vector2 targetPos = mapTarget.getComponent(Position.class).position;
            dragOffset = new Vector2(targetPos.x - gameVec.x, targetPos.y - gameVec.y);
        }
        else {
            throw new RuntimeException("Cannot set drag offset when the editor has no target");
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (editor.isMapInputEnabled()) {
            Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(screenX, screenY, 0f));
            Entity mapTarget = editor.getMapTarget();

            if (mapTarget != null) {
                HashMap<Integer, Body> map = editor.getWorld().getSystem(BodyManager.class).getBodies();
                Body body = map.get(mapTarget.getId());
                body.setTransform(gameVec.x + dragOffset.x, gameVec.y + dragOffset.y, body.getAngle());
                editor.updateTargetTransform();
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (editor.isMapInputEnabled()) {
            OrthographicCamera camera = editor.getGameCamera();
            System.out.println(amount * ZOOM_FACTOR);
            camera.zoom += amount * ZOOM_FACTOR;
            if (camera.zoom <= 0) camera.zoom = MapEditor.MIN_ZOOM;
            camera.update();
        }
        return false;
    }
}
