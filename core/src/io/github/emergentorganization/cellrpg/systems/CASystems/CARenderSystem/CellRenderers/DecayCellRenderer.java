package com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.CAGridComponents;


public class DecayCellRenderer implements iCellRenderer {

    public static final Color[] stateColorMap = {
            // group 1: white/gray
            null,
            new Color(1f, 1f, 1f, .05f),
            new Color(1f, 1f, 1f, .1f),
            new Color(1f, 1f, 1f, .2f),
            new Color(1f, 1f, 1f, .3f),
            new Color(1f, 1f, 1f, .4f),
            new Color(1f, 1f, 1f, .5f),
            new Color(1f, 1f, 1f, .6f),
            new Color(1f, 1f, 1f, .7f),
            new Color(1f, 1f, 1f, .8f),
            new Color(1f, 1f, 1f, .9f),
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

    public static int getMaxOfColorGroup(colorGroupKeys groupKey) {
        // returns the index of the highest state in the given color group
        switch (groupKey) {
            case WHITE:
                return 10;
            case BLUE:
                return 14;
            case FIRE:
                return 19;
            default:
                return 23;
        }
    }

    @Override
    public void renderCell(ShapeRenderer shapeRenderer, CAGridComponents gridComps, final int i, final int j,
                           final float x_origin, final float y_origin) {
        int state = gridComps.states[i][j].getState();
        if (gridComps.stateColorMap[state] != null) {
            // draw square
            shapeRenderer.setColor(gridComps.stateColorMap[state]);
            drawSquare(i, j, shapeRenderer, gridComps, x_origin, y_origin);
        }
    }

    private void drawSquare(final int i, final int j, ShapeRenderer shapeRenderer, CAGridComponents gridComps,
                            final float x_origin, final float y_origin) {
        float x = i * (gridComps.cellSize + 1) + x_origin;  // +1 for cell border
        float y = j * (gridComps.cellSize + 1) + y_origin;
        shapeRenderer.rect(x, y, gridComps.cellSize, gridComps.cellSize);
    }

    public static enum colorGroupKeys {
        WHITE, BLUE, FIRE
    }
}
