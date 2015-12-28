package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;

/**
 * Created by brian on 11/22/15.
 */
public class BoundsBox extends Rectangle implements Renderable {
    private final BoundsGizmo.GizmoTrigger trigger;
    private final Vector2 bl = new Vector2();
    private final Vector2 br = new Vector2();
    private final Vector2 tl = new Vector2();
    private final Vector2 tr = new Vector2();

    public BoundsBox(Vector2 pos, Vector2 size, final BoundsGizmo.GizmoTrigger alignment) {
        super(pos.x, pos.y, size.x, size.y);
        this.trigger = alignment;
        updatePoints();
    }

    private void updatePoints() {
        bl.set(x, y);
        br.set(x + width, y);
        tl.set(x, y + height);
        tr.set(x + width, y + height);
    }

    public BoundsGizmo.GizmoTrigger getTrigger() {
        return trigger;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.rectLine(bl, br, MapEditor.BB_THICKNESS);
        shapeRenderer.rectLine(br, tr, MapEditor.BB_THICKNESS);
        shapeRenderer.rectLine(tr, tl, MapEditor.BB_THICKNESS);
        shapeRenderer.rectLine(tl, bl, MapEditor.BB_THICKNESS);
    }

    @Override
    public Rectangle set(float x, float y, float width, float height) {
        super.set(x, y, width, height);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setX(float x) {
        super.setX(x);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setY(float y) {
        super.setY(y);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setWidth(float width) {
        super.setWidth(width);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setHeight(float height) {
        super.setHeight(height);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setPosition(float x, float y) {
        super.setPosition(x, y);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setPosition(Vector2 position) {
        super.setPosition(position);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setSize(float sizeXY) {
        super.setSize(sizeXY);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setSize(float width, float height) {
        super.setSize(width, height);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle set(Rectangle rect) {
        super.set(rect);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setCenter(float x, float y) {
        super.setCenter(x, y);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setCenter(Vector2 position) {
        super.setCenter(position);
        updatePoints();
        return this;
    }
}
