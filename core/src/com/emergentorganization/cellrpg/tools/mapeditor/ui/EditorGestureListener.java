package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;

/**
 * Created by Brian on 6/24/2015.
 */
public class EditorGestureListener implements GestureDetector.GestureListener {
    private OrthographicCamera camera;
    public static float ZOOM_FACTOR = 0.05f;

    public EditorGestureListener(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        float diff = initialDistance - distance;
        diff *= ZOOM_FACTOR;
        camera.zoom += diff;
        if (camera.zoom <= 0f) camera.zoom = MapEditor.MIN_ZOOM;
        camera.update();

        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
