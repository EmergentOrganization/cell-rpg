package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by OrelBitton on 23/06/2015.
 */
public interface BaseComponent {

    public void added();

    public void update(float deltaTime);

    public void render(SpriteBatch batch);

    public boolean shouldRender();

    public void debugRender(ShapeRenderer renderer);

    public boolean shouldDebugRender();

    public void dispose();

}
