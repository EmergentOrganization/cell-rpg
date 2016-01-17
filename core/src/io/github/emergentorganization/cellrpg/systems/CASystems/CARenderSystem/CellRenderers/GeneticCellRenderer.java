package com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCell;


public class GeneticCellRenderer implements iCellRenderer {
    @Override
    public void renderCell(ShapeRenderer shapeRenderer, CAGridComponents gridComps, final int i, final int j,
                           final float x_origin, final float y_origin) {
        if (gridComps.getState(i, j) != 0) {  // state must be > 0 else stateColorMap indexError
            // draw square
            GeneticCell cell = (GeneticCell) gridComps.states[i][j];
            shapeRenderer.setColor(cell.getColor());

            float x = i * (gridComps.cellSize + 1) + x_origin;  // +1 for cell border
            float y = j * (gridComps.cellSize + 1) + y_origin;
            shapeRenderer.rect(x, y, gridComps.cellSize, gridComps.cellSize);
        }
    }
}
