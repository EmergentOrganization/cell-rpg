package com.emergentorganization.cellrpg.tools.mapeditor.renderables;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;


public class BoundsBox extends Rectangle implements Renderable {
    private final Vector2 bl = new Vector2();
    private final Vector2 br = new Vector2();
    private final Vector2 tl = new Vector2();
    private final Vector2 tr = new Vector2();
    private Corner corner;

    public BoundsBox(final Vector2 pos, final Vector2 size) {
        super(pos.x, pos.y, size.x, size.y);
        updatePoints();
    }

    private void updatePoints() {
        bl.set(x, y);
        br.set(x + width, y);
        tl.set(x, y + height);
        tr.set(x + width, y + height);
    }

    @Override
    public void render(final ShapeRenderer shapeRenderer) {
        shapeRenderer.rectLine(bl, br, MapEditor.BB_THICKNESS);
        shapeRenderer.rectLine(br, tr, MapEditor.BB_THICKNESS);
        shapeRenderer.rectLine(tr, tl, MapEditor.BB_THICKNESS);
        shapeRenderer.rectLine(tl, bl, MapEditor.BB_THICKNESS);
    }

    @Override
    public Rectangle set(final float x, final float y, final float width, final float height) {
        super.set(x, y, width, height);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setX(final float x) {
        super.setX(x);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setY(final float y) {
        super.setY(y);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setWidth(final float width) {
        super.setWidth(width);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setHeight(final float height) {
        super.setHeight(height);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setPosition(final float x, final float y) {
        super.setPosition(x, y);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setPosition(final Vector2 position) {
        super.setPosition(position);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setSize(final float sizeXY) {
        super.setSize(sizeXY);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setSize(final float width, final float height) {
        super.setSize(width, height);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle set(final Rectangle rect) {
        super.set(rect);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setCenter(final float x, final float y) {
        super.setCenter(x, y);
        updatePoints();
        return this;
    }

    @Override
    public Rectangle setCenter(final Vector2 position) {
        super.setCenter(position);
        updatePoints();
        return this;
    }
}
