package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.entities.ZIndex;

/**
 * Created by 7yl4r on 9/18/2015.
 */
public class GeneticCAGrid extends CAGridBase{
    public GeneticCAGrid(int sizeOfCells, ZIndex z_index) {
        /*
        @sizeOfCells     : display size of an individual cell
        @z_index         : ZIndex level to render the grid
         */
        super(sizeOfCells, z_index, new Color[]{});
    }

    @Override
    public int getLastState(final int row, final int col){
        try {
            CellWithHistory cell = (CellWithHistory) states[row][col];
            return cell.getLastState();
        } catch (IndexOutOfBoundsException err){
            return 0;  // if out-of-bounds, assume state=0
        }
    }

    @Override
    protected void generate() {
        super.generate();
        // generates the next frame of the CA
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                CellWithHistory cell = (CellWithHistory) states[i][j];
                cell.setLastState(states[i][j].getState());
            }
        }

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                //System.out.print(i + "," + j +'\t');
                setState(i, j, ca_rule(i, j));
            }
        }
    }

    @Override
    protected BaseCell newCell(int init_state){
        return new GeneticCell(init_state);
    }

    @Override
    protected void renderCell(final int i, final int j, ShapeRenderer shapeRenderer,
                              final float x_origin, final float y_origin){
        if (getState(i,j) != 0) {  // state must be > 0 else stateColorMap indexError
            // draw square
            GeneticCell cell = (GeneticCell) states[i][j];
            shapeRenderer.setColor(cell.getColor());

            float x = i * (cellSize + 1) + x_origin;  // +1 for cell border
            float y = j * (cellSize + 1) + y_origin;
            shapeRenderer.rect(x, y, cellSize, cellSize);
        }
    }
}
