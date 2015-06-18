package com.emergentorganization.cellrpg.tools.mapeditor;

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
import org.dyn4j.dynamics.RaycastResult;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/15/2015.
 */
public class EditorInputProcessor implements InputProcessor {
    private MapEditor editor;

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
        editor.rayStart.set(gameVec.x - 5f, gameVec.y);
        editor.rayEnd.set(gameVec.x + 5f, gameVec.y);

        ArrayList<RaycastResult> results = new ArrayList<RaycastResult>();

        boolean intersect = editor.getWorld().raycast(new org.dyn4j.geometry.Vector2(gameVec.x - 5f, gameVec.y),
                new org.dyn4j.geometry.Vector2(gameVec.x + 5f, gameVec.y), false, false, results);
        // TODO: Raycasting not consistent

        if (intersect) {
            Entity entity = ((CellUserData) results.get(0).getBody().getUserData()).entity;
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
        else {
            editor.setMapTarget(null);
        }
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
