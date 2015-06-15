package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;

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
        // TODO: If raycast doesn't hit object that isn't background layer, spawn a new object
        try {
            Entity entity = editor.getSelectedItem().entity.newInstance();

            Matrix3 transform = editor.getNewObjectTransform();
            MovementComponent mc = entity.getMovementComponent();

            mc.setScale(transform.getScale(new Vector2()));
            mc.setRotation(transform.getRotation());

            Vector3 pos = new Vector3(screenX, screenY, 0f);
            Vector3 unproject = editor.getGameCamera().unproject(pos);
            Vector2 mousePos = new Vector2(unproject.x, unproject.y);
            mc.setWorldPosition(transform.getTranslation(new Vector2()).add(mousePos));

            editor.addEntity(entity);

            //System.out.println("Spawning " + entity.getClass().getName());
            //System.out.println(transform);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
