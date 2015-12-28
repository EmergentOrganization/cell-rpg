package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 11/22/15.
 */
public class BoundsGizmo implements Renderable {
    public static final float GIZMO_LENGTH = 0.13f;
    private static final Vector2 GIZMO_SIZE = new Vector2(GIZMO_LENGTH, GIZMO_LENGTH);
    public enum GizmoTrigger {
        BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP_LEFT, TOP_RIGHT, CENTER
    }
    private BoundsBox[] gizmos;
    private BoundsBox boundsBox;
    private Vector2 pos;
    private Vector2 size;

    public BoundsGizmo(Vector2 size, Vector2 pos) {
        this.pos = pos;
        this.size = size;
        boundsBox = new BoundsBox(pos, size, GizmoTrigger.CENTER);
        gizmos = new BoundsBox[] {
                new BoundsBox(pos.cpy(), GIZMO_SIZE, GizmoTrigger.BOTTOM_LEFT),
                new BoundsBox(pos.cpy().add(size.x, 0), GIZMO_SIZE, GizmoTrigger.BOTTOM_RIGHT),
                new BoundsBox(pos.cpy().add(0, size.y), GIZMO_SIZE, GizmoTrigger.TOP_LEFT),
                new BoundsBox(pos.cpy().add(size), GIZMO_SIZE, GizmoTrigger.TOP_RIGHT)
        };
    }

    public void setPosition(Vector2 pos) {
        this.pos.set(pos);
        boundsBox.setPosition(pos.cpy());
        gizmos[0].setCenter(pos.cpy());
        gizmos[1].setCenter(pos.cpy().add(size.x, 0));
        gizmos[2].setCenter(pos.cpy().add(0, size.y));
        gizmos[3].setCenter(pos.cpy().add(size));
    }

    /**
     * Detects a hit on any vertex gizmo
     * @param rectangle The hit to compare against
     * @return The vertex gizmo position on the bounds collider, or null if none were hit
     */
    public GizmoTrigger detectContains(Rectangle rectangle) {
        for (BoundsBox gizmo : gizmos) {
            boolean contains = gizmo.contains(rectangle);
            if (contains) {
                return gizmo.getTrigger();
            }
        }
        return null;
    }

    public void moveVertexGizmo(GizmoTrigger trigger, Vector2 newPos) {
        for (BoundsBox gizmo : gizmos) {
            if (gizmo.getTrigger() == trigger) {
                gizmo.setPosition(newPos);
            }
        }

    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        boundsBox.render(shapeRenderer);
        for (BoundsBox gizmo : gizmos) {
            gizmo.render(shapeRenderer);
        }
    }
}
