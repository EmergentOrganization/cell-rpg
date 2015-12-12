package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 11/22/15.
 */
public class BoundsGizmo implements Renderable {
    public enum GizmoTrigger {
        BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP_LEFT, TOP_RIGHT
    }
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
                new VertexGizmo(pos.cpy().sub(halfSize), GizmoTrigger.BOTTOM_LEFT),
                new VertexGizmo(pos.cpy().add(halfSize.x, -halfSize.y), GizmoTrigger.BOTTOM_RIGHT),
                new VertexGizmo(pos.cpy().add(-halfSize.x, halfSize.y), GizmoTrigger.TOP_LEFT),
                new VertexGizmo(pos.cpy().add(halfSize), GizmoTrigger.TOP_RIGHT)
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

    /**
     * Detects a hit on any vertex gizmo
     * @param rectangle The hit to compare against
     * @return The vertex gizmo position on the bounds collider, or null if none were hit
     */
    public GizmoTrigger detectContains(Rectangle rectangle) {
        for (VertexGizmo gizmo : gizmos) {
            boolean contains = gizmo.contains(rectangle);
            if (contains)
                return gizmo.getTrigger();
        }
        return null;
    }

    public void moveVertexGizmo(GizmoTrigger trigger, Vector2 newPos) {
        for (VertexGizmo gizmo : gizmos) {
            if (gizmo.getTrigger() == trigger) {
                gizmo.setPosition(newPos);
            }
        }

    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        boundsRect.render(shapeRenderer);
        for (VertexGizmo gizmo : gizmos) {
            gizmo.render(shapeRenderer);
        }
    }
}
