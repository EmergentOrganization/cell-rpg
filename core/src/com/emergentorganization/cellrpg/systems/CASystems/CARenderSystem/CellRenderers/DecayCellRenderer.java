package com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Created by 7yl4r on 12/15/2015.
 */
public class DecayCellRenderer implements iCellRenderer {

    public static enum colorGroupKeys{
        WHITE, BLUE, FIRE
    }

    public static final Color[] stateColorMap = {
        // group 1: white/gray
        null,
        new Color(1f, 1f, 1f, .2f),
        new Color(1f, 1f, 1f, .4f),
        new Color(1f, 1f, 1f, .8f),
        // group 2: light blue
        null,
        new Color(0f, .7f, 1f, .2f),
        new Color(0f, .7f, 1f, .4f),
        new Color(0f, .7f, 1f, .8f),
        // yellow-orange-red
        null,
        new Color(1f, 1f, 0f, .1f),  // yellow
        new Color(1f, .5f, 0f, .2f), // orange
        new Color(1f, 0f, 0f, .4f), // red
        new Color(.5f, 0f, 0f, .8f), // dark red
        // red markers (for errors)
        null,
        new Color(1f, 0f, 0f, 1f), // red
        new Color(1f, 0f, 0f, 1f), // red
        new Color(1f, 0f, 0f, 1f), // red
    };

    public static int getMaxOfColorGroup(colorGroupKeys groupKey){
        // returns the index of the highest state in the given color group
        switch (groupKey){
            case WHITE:
                return 3;
            case BLUE:
                return 7;
            case FIRE:
                return 12;
            default:
                return 16;
        }
    }

    @Override
    public void renderCell(ShapeRenderer shapeRenderer, CAGridComponents gridComps, final int i, final int j,
                            final float x_origin, final float y_origin){
        int state = gridComps.states[i][j].getState();
        if (gridComps.stateColorMap[state] != null) {
            if (state > 2) {
                System.out.println("cellRenderState:" + state);
            }
            // draw square
            shapeRenderer.setColor(gridComps.stateColorMap[state]);
            drawSquare(i, j, shapeRenderer, gridComps, x_origin, y_origin);
        }
    }

    private void drawSquare(final int i, final int j, ShapeRenderer shapeRenderer, CAGridComponents gridComps,
                            final float x_origin, final float y_origin){
        float x = i * (gridComps.cellSize + 1) + x_origin;  // +1 for cell border
        float y = j * (gridComps.cellSize + 1) + y_origin;
        shapeRenderer.rect(x, y, gridComps.cellSize, gridComps.cellSize);
    }
}
