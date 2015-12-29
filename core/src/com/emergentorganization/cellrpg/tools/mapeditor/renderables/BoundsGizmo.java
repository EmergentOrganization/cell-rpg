package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brian on 11/22/15.
 */
public class BoundsGizmo implements Renderable {
    public static final float GIZMO_LENGTH = 0.20f;
    private static final Vector2 GIZMO_SIZE = new Vector2(GIZMO_LENGTH, GIZMO_LENGTH);
    private final CornerGizmo[] gizmos;
    private final BoundsBox boundsBox;
    private final Vector2 pos;
    private final Vector2 size;

    public BoundsGizmo(final Vector2 size, final Vector2 pos) {
        this.pos = pos;
        this.size = size;
        boundsBox = new BoundsBox(pos, size);
        gizmos = new CornerGizmo[] {
                new CornerGizmo(pos.cpy(), GIZMO_SIZE, Corner.BOTTOM_LEFT),
                new CornerGizmo(pos.cpy().add(size.x, 0), GIZMO_SIZE, Corner.BOTTOM_RIGHT),
                new CornerGizmo(pos.cpy().add(0, size.y), GIZMO_SIZE, Corner.TOP_LEFT),
                new CornerGizmo(pos.cpy().add(size), GIZMO_SIZE, Corner.TOP_RIGHT)
        };
    }

    public void setPosition(final Vector2 pos) {
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
    public CornerGizmo detectContains(final Rectangle rectangle) {
        for (final CornerGizmo gizmo : gizmos) {
            final boolean contains = gizmo.contains(rectangle);
            if (contains) {
                return gizmo;
            }
        }
        return null;
    }

    public void moveVertexGizmo(final Corner corner, final Vector2 newPos) {
        for (final CornerGizmo gizmo : gizmos) {
            if (gizmo.getCorner() == corner) {
                gizmo.setPosition(newPos);
            }
        }

    }

    @Override
    public void render(final ShapeRenderer shapeRenderer) {
        boundsBox.render(shapeRenderer);
        for (final BoundsBox gizmo : gizmos) {
            gizmo.render(shapeRenderer);
        }
    }
}
