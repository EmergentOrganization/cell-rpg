package io.github.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.math.Vector2;


public class CornerGizmo extends BoundsBox {
    private final Corner corner;

    public CornerGizmo(final Vector2 pos, final Vector2 size, final Corner corner) {
        super(pos, size);
        this.corner = corner;
    }

    public Corner getCorner() {
        return corner;
    }
}
