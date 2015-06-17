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
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.AABB;

import java.util.List;

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
        Vector3 screenVec = editor.getUiStage().getCamera().unproject(new Vector3(screenX, screenY, 0f));
        /*Ray ray = new Ray(new org.dyn4j.geometry.Vector2(mousePos.x, mousePos.y), new org.dyn4j.geometry.Vector2(0, -1));

        ArrayList<RaycastResult> results = new ArrayList<RaycastResult>();
        editor.getWorld().raycast(ray, 1000d, false, false, results);*/

        //if (results.get(0)) // TODO: configure entities to store refs in userdata of body

        if (button == Input.Buttons.LEFT) {
            editor.setLastLMBClick(new Vector2(screenVec.x, screenVec.y));

            Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(screenX, screenY, 0f));
            AABB ab = new AABB(10f);
            ab.translate(new org.dyn4j.geometry.Vector2(gameVec.x, gameVec.y));
            List<Body> results = editor.getWorld().detect(ab);

            if (results.size() > 0) {
                Entity entity = ((CellUserData) results.get(0).getUserData()).entity;
                SpriteComponent sc = (SpriteComponent) entity.getFirstComponentByType(ComponentType.SPRITE);
                if (sc != null) {
                    Sprite sprite = sc.getSprite();
                    Vector2 size = new Vector2(sprite.getWidth() * sprite.getScaleX(),
                                                sprite.getHeight() * sprite.getScaleY());
                    editor.setMapTarget(new MapTarget(entity, size, entity.getMovementComponent()));
                }
            }
            else {
                editor.setMapTarget(null);
            }
        }
        else if (button == Input.Buttons.RIGHT) {
            editor.setLastRMBClick(new Vector2(screenVec.x, screenVec.y));
            editor.openContextMenu();
        }

        return false;
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
