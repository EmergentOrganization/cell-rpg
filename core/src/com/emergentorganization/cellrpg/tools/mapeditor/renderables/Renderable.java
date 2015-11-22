package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 11/22/15.
 */
public interface Renderable {
    void setPosition(Vector2 pos);

    void render(ShapeRenderer shapeRenderer);
}
