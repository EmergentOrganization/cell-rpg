package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * Created by tylar on 2015-07-06.
 */
public class decayCAGrid extends CAGridBase {
    public decayCAGrid(int sizeOfCells, ZIndex z_index, Color[] state_color_map) {
        /*
        @sizeOfCells     : display size of an individual cell
        @z_index         : ZIndex level to render the grid
        @state_color_map : list of colors which correspond to ca states  TODO: use a CAColorDefinition class instead
         */
        super(sizeOfCells, z_index, state_color_map);
    }

    @Override
    protected void generate() {
        super.generate();
        // generates the next frame of the CA
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j].setState(states[i][j].getState() - 1);
            }
        }
    }

    public int getLastState(final int row, final int col){
        // with no buffer there is no last state, just use current
        return getState(row, col);
    }

    private void drawSquare(final int i, final int j, ShapeRenderer shapeRenderer, final float x_origin, final float y_origin){
        float x = i * (cellSize + 1) + x_origin;  // +1 for cell border
        float y = j * (cellSize + 1) + y_origin;
        shapeRenderer.rect(x, y, cellSize, cellSize);
    }
    @Override
    protected void renderCell(final int i, final int j, ShapeRenderer shapeRenderer,
                            final float x_origin, final float y_origin){
        final int min_render = -10;
        final float delt = .1f;
        if (states[i][j].getState() > 0) {  // state must be > 0 else stateColorMap indexError
            // draw square
            shapeRenderer.setColor(stateColorMap[states[i][j].getState()-1]);
            drawSquare(i, j, shapeRenderer, x_origin, y_origin);
        } else if (states[i][j].getState() > min_render){
            Color c = new Color(stateColorMap[0]);
            float dc = delt * (float)Math.abs(states[i][j].getState());
            c = c.sub(0, 0, 0, dc);
            shapeRenderer.setColor(c);
            drawSquare(i, j, shapeRenderer, x_origin, y_origin);
        }  // else don't bother
    }
}