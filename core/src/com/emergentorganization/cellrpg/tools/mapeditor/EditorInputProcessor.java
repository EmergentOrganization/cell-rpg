package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.geometry.Ray;

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
        Vector3 screenVec = editor.getUiStage().getCamera().unproject(new Vector3(screenX, screenY, 0f));
        /*Ray ray = new Ray(new org.dyn4j.geometry.Vector2(mousePos.x, mousePos.y), new org.dyn4j.geometry.Vector2(0, -1));

        ArrayList<RaycastResult> results = new ArrayList<RaycastResult>();
        editor.getWorld().raycast(ray, 1000d, false, false, results);*/

        //if (results.get(0)) // TODO: configure entities to store refs in userdata of body

        if (button == Input.Buttons.LEFT) {
            editor.setLastLMBClick(new Vector2(screenVec.x, screenVec.y));

            Vector3 gameVec = editor.getGameCamera().unproject(new Vector3(screenX, screenY, 0f));
            Ray ray = new Ray(new org.dyn4j.geometry.Vector2(gameVec.x - 10f, gameVec.y), new org.dyn4j.geometry.Vector2(1, 0));
            editor.rayStart.set(gameVec.x - 2f, gameVec.y);
            editor.rayEnd.set(gameVec.x + 2f, gameVec.y);

            ArrayList<RaycastResult> results = new ArrayList<RaycastResult>();
            editor.getWorld().raycast(ray, 4d, false, false, results);

            if (results.size() > 0) System.out.println("Hit something!!");
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

    private void addSelectedEntity(Vector2 mousePos) throws IllegalAccessException, InstantiationException {
        Entity entity = editor.getSelectedItem().entity.newInstance();

        Matrix3 transform = editor.getNewObjectTransform();
        MovementComponent mc = entity.getMovementComponent();

        mc.setScale(transform.getScale(new Vector2()));
        mc.setRotation(transform.getRotation());
        mc.setWorldPosition(transform.getTranslation(new Vector2()).add(mousePos));


        editor.addEntity(entity);
    }
}
