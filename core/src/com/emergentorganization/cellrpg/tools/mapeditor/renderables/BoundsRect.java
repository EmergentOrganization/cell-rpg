package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;

/**
 * Created by brian on 11/22/15.
 */
public class BoundsRect implements Renderable {
    private final Vector2 pos;
    private Vector2 size;
    private Vector2 halfSize;

    public BoundsRect(Vector2 size, Vector2 pos) {
        this.pos = pos;
        this.size = size;
        this.halfSize = size.cpy().scl(0.5f);
    }

    @Override
    public void setPosition(Vector2 pos) {
        this.pos.set(pos);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.rectLine(pos.x - halfSize.x, pos.y - halfSize.y, pos.x + halfSize.x, pos.y - halfSize.y, MapEditor.BB_THICKNESS); // bl to br
        shapeRenderer.rectLine(pos.x + halfSize.x, pos.y - halfSize.y, pos.x + halfSize.x, pos.y + halfSize.y, MapEditor.BB_THICKNESS); // br to tr
        shapeRenderer.rectLine(pos.x + halfSize.x, pos.y + halfSize.y, pos.x - halfSize.x, pos.y + halfSize.y, MapEditor.BB_THICKNESS); // tr to tl
        shapeRenderer.rectLine(pos.x - halfSize.x, pos.y + halfSize.y, pos.x - halfSize.x, pos.y - halfSize.y, MapEditor.BB_THICKNESS); // tl to bl
    }

    public Vector2 getPosition() {
        return pos;
    }

    public Vector2 getSize() {
        return size;
    }
}
