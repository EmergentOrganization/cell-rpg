package com.emergentorganization.cellrpg.input.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * static utility class for player input helpers
 */
public class inputUtil {
    public static Vector2 getMousePos(Camera cam) {
        Vector3 mousePos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Vector2(mousePos.x, mousePos.y);
    }
}
