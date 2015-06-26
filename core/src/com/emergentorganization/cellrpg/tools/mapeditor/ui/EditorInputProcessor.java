package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.entity.ComponentType;
import com.emergentorganization.cellrpg.components.entity.SpriteComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.physics.CellUserData;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.AABB;

import java.util.List;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EditorInputProcessor implements InputProcessor {
    private MapEditor editor;
    public static float HIT_ACCURACY = 5f; // lower the value, the more accurate the hit detection
    public static float ZOOM_AMT = 0.1f; // amount of zoom per keypress
    private Vector2 dragOffset = new Vector2();

    public EditorInputProcessor(MapEditor editor) {
        this.editor = editor;
    }

    @Override
    public boolean keyDown(int keycode) {
        OrthographicCamera camera = editor.getGameCamera();
        if (keycode == Input.Keys.Z) { // zoom in
            camera.zoom -= ZOOM_AMT;
        }
        else if (keycode == Input.Keys.X) { // zoom out
            camera.zoom += ZOOM_AMT;
        }

        if (camera.zoom <= 0f) camera.zoom = MapEditor.MIN_ZOOM;
        camera.update();
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

        Vector2 screenCoords = new Vector2(screenX, screenY);
        if (button == Input.Buttons.LEFT) {
            onLeftClick(screenCoords);
        }
        else if (button == Input.Buttons.RIGHT) {
            onRightClick(screenCoords);
        }

        return false;
    }

    private void onLeftClick(Vector2 screenCoords) {
        Vector3 uiVec = editor.getUiStage().getCamera().unproject(new Vector3(screenCoords, 0f));
        editor.setLastLMBClick(new Vector2(uiVec.x, uiVec.y));

        Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(screenCoords.x, screenCoords.y, 0f));
        AABB box = new AABB(HIT_ACCURACY);
        org.dyn4j.geometry.Vector2 point = new org.dyn4j.geometry.Vector2(gameVec.x, gameVec.y);
        box.translate(point);

        editor.getWorld().update(Gdx.graphics.getDeltaTime());
        List<Body> detect = editor.getWorld().getBroadphaseDetector().detect(box);

        boolean foundBody = false;
        for (Body body : detect) {
            if (body.contains(point)) {
                foundBody = true;

                Entity entity = ((CellUserData) body.getUserData()).entity;
                SpriteComponent sc = (SpriteComponent) entity.getFirstComponentByType(ComponentType.SPRITE);
                SpriteComponent gc = (SpriteComponent) entity.getFirstComponentByType(ComponentType.GRAPHICS);
                if (sc != null) {
                    boolean switched = switchMapTarget(sc.getSprite(), entity);
                    setDragOffset(screenCoords);
                    if (switched) break;
                }
                else if (gc != null) {
                    boolean switched = switchMapTarget(gc.getSprite(), entity);
                    setDragOffset(screenCoords);
                    if (switched) break;
                }
                else {
                    throw new RuntimeException("Cannot select a component with no render-able component");
                }
            }
        }

        if (!foundBody) editor.setMapTarget(null);
    }

    /**
     * Handles selecting map targets and switching to a different one if one is already selected in overlay instances
     * @param sprite Sprite to switch to
     * @param entity Entity to switch to
     * @return whether or not a switch happened
     */
    private boolean switchMapTarget(Sprite sprite, Entity entity) {
        if (editor.getMapTarget() != null) {
            if (entity != editor.getMapTarget().target) {
                setMapTarget(sprite, entity);
                return true;
            }
            else return false;
        }
        else {
            setMapTarget(sprite, entity);
            return true;
        }
    }

    private void onRightClick(Vector2 screenPos) {
        Vector3 worldPos = editor.getUiStage().getCamera().unproject(new Vector3(screenPos.x, screenPos.y, 0f));
        editor.setLastRMBClick(new Vector2(worldPos.x, worldPos.y));
        editor.openContextMenu();
    }

    private void setDragOffset(Vector2 mousePos) {
        Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(mousePos.x, mousePos.y, 0f));
        MapTarget mapTarget = editor.getMapTarget();

        if (mapTarget != null) {
            Vector2 targetPos = mapTarget.movementComponent.getWorldPosition();
            dragOffset = new Vector2(targetPos.x - gameVec.x, targetPos.y - gameVec.y);
        }
        else {
            throw new RuntimeException("Cannot set drag offset when the editor has no target");
        }
    }

    private void setMapTarget(Sprite sprite, Entity entity) {
        Vector2 size = new Vector2(sprite.getWidth() * sprite.getScaleX(),
                sprite.getHeight() * sprite.getScaleY());
        editor.setMapTarget(new MapTarget(entity, size, entity.getMovementComponent()));
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(screenX, screenY, 0f));
        MapTarget mapTarget = editor.getMapTarget();

        if (mapTarget != null) {
            mapTarget.movementComponent.setWorldPosition(gameVec.x + dragOffset.x, gameVec.y + dragOffset.y);
            editor.updateTargetTransform();
            return false;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        OrthographicCamera camera = editor.getGameCamera();
        camera.zoom += amount;
        if (camera.zoom <= 0) camera.zoom = MapEditor.MIN_ZOOM;
        else {
            camera.zoom = (float) Math.floor(camera.zoom);
        }
        camera.update();
        return false;
    }
}
