package com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Created by 7yl4r on 12/15/2015.
 */
public class DecayCellRenderer implements ICellRenderer {

    private final int MIN_RENDER = -5;
    private final float DELT = 1f/(float) MIN_RENDER;

    @Override
    public void renderCell(ShapeRenderer shapeRenderer, CAGridComponents gridComps, final int i, final int j,
                            final float x_origin, final float y_origin){
        int state = gridComps.states[i][j].getState();
//        System.out.println("cellRenderState:"+state);
        if (state > 0) {  // state must be > 0 else stateColorMap indexError
            // draw square
            shapeRenderer.setColor(gridComps.stateColorMap[gridComps.states[i][j].getState()-1]);
            drawSquare(i, j, shapeRenderer, gridComps, x_origin, y_origin);
        } else if (state > MIN_RENDER){
            shapeRenderer.setColor(new Color(
                    gridComps.stateColorMap[0].r,
                    gridComps.stateColorMap[0].g,
                    gridComps.stateColorMap[0].b,
                    gridComps.stateColorMap[0].a - DELT * (float)state
            ));
            drawSquare(i, j, shapeRenderer, gridComps, x_origin, y_origin);
        }  // else don't bother
    }

    private void drawSquare(final int i, final int j, ShapeRenderer shapeRenderer, CAGridComponents gridComps,
                            final float x_origin, final float y_origin){
        float x = i * (gridComps.cellSize + 1) + x_origin;  // +1 for cell border
        float y = j * (gridComps.cellSize + 1) + y_origin;
        shapeRenderer.rect(x, y, gridComps.cellSize, gridComps.cellSize);
    }
}
