package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.SpriteComponent;
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
                    setMapTarget(sc.getSprite(), entity);
                }
                else if (gc != null) {
                    setMapTarget(gc.getSprite(), entity);
                }
                else {
                    throw new RuntimeException("Cannot select a component with no render-able component");
                }
            }
        }

        if (!foundBody) editor.setMapTarget(null);
    }

    private void onRightClick(Vector2 screenPos) {
        Vector3 worldPos = editor.getUiStage().getCamera().unproject(new Vector3(screenPos.x, screenPos.y, 0f));
        editor.setLastRMBClick(new Vector2(worldPos.x, worldPos.y));
        editor.openContextMenu();
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
        if (camera.zoom <= 0) camera.zoom = 0.1f;
        else {
            camera.zoom = (float) Math.floor(camera.zoom);
        }
        camera.update();
        return false;
    }
}
