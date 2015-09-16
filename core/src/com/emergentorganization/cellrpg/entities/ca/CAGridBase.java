package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * defines how to interact with a CAGrid.
 * Created by 7yl4r on 9/16/2015.
 */
public abstract class CAGridBase extends Entity {

    public CAGridBase(ZIndex z_index){
        super(z_index);
    }
    /*
        @z_index         : ZIndex level to render the grid
     */

    public abstract long getGenerationNumber();
    // returns current generation number

    public abstract void resetGenerationNumber();
    // resets generation counter to

    public abstract int getSizeX();
    // returns grid size (number of cells) in x-dimension

    public abstract int getSizeY();
    // returns grid size (number of cells) in y-dimension

    public abstract int getState(final float x, final float y);
    // returns state of cell nearest to given world-coordinates

    public abstract long stampState(final int[][] pattern, final int row, final int col);
    public abstract long stampState(final int[][] pattern, final int row, final int col, final BaseCell[][] cellStates);
    // stamps a pattern onto the grid at given row, col position

    public abstract long stampState(final int[][] pattern, final float x, final float y);
    public abstract long stampState(final int[][] pattern, Vector2 position);
    // stamps a pattern onto the grid centered at the nearest grid cells to the given world position



    /* === ENTITY METHODS (PROBABLY) OVERRIDDEN BY IMPLEMENTER === */

    @Override
    public void update(float deltaTime){
    // updates the grid. (probably) computes ca generations.
        super.update(deltaTime);
    }

    @Override
    public void added(){
        super.added();
    }

    @Override
    public void debugRender(ShapeRenderer shapeRenderer){
        super.debugRender(shapeRenderer);
    }
}
