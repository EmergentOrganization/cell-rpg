package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 11/22/15.
 */
public class BoundsGizmo implements Renderable {
    private VertexGizmo[] gizmos;
    private BoundsRect boundsRect;
    private Vector2 pos;
    private Vector2 size;
    private Vector2 halfSize;

    public BoundsGizmo(Vector2 size, Vector2 pos) {
        this.pos = pos;
        this.size = size;
        this.halfSize = size.cpy().scl(0.5f);
        boundsRect = new BoundsRect(size, pos);
        gizmos = new VertexGizmo[] {
                new VertexGizmo(pos.cpy().sub(halfSize)),                   // bl
                new VertexGizmo(pos.cpy().add(halfSize.x, -halfSize.y)),     // br
                new VertexGizmo(pos.cpy().add(-halfSize.x, halfSize.y)),                  // tl
                new VertexGizmo(pos.cpy().add(halfSize))              // tr
        };
    }

    @Override
    public void setPosition(Vector2 pos) {
        this.pos.set(pos);
        boundsRect.setPosition(pos);
        gizmos[0].setPosition(pos.cpy().sub(halfSize));
        gizmos[1].setPosition(pos.cpy().add(halfSize.x, -halfSize.y));
        gizmos[2].setPosition(pos.cpy().add(-halfSize.x, halfSize.y));
        gizmos[3].setPosition(pos.cpy().add(halfSize));
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        boundsRect.render(shapeRenderer);
        for (VertexGizmo gizmo : gizmos) {
            gizmo.render(shapeRenderer);
        }
    }
}
