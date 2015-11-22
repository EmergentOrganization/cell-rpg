package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 11/22/15.
 */
public class VertexGizmo extends BoundsRect {
    public static final float GIZMO_SIZE = 0.13f;
    private Rectangle rectangle;
    private BoundsRect boundsRect;

    public VertexGizmo(Vector2 pos) {
        super(new Vector2(GIZMO_SIZE, GIZMO_SIZE), pos);
        this.rectangle = new com.badlogic.gdx.math.Rectangle(pos.x, pos.y, GIZMO_SIZE, GIZMO_SIZE);
        boundsRect = new BoundsRect(getSize(), pos);
    }

    public boolean contains(com.badlogic.gdx.math.Rectangle other) {
        return rectangle.contains(other);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        boundsRect.render(shapeRenderer);
    }
}
