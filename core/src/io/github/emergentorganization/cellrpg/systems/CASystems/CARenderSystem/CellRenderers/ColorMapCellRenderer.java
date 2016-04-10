package io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple cell renderer which renders a cell using a state-to-color colorMap.
 */
public class ColorMapCellRenderer implements iCellRenderer {
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    public void renderCell(ShapeRenderer renderer, CAGridComponents layerComponents, final int i, final int j,
                           final float x_origin, final float y_origin) {
        try {
            if (layerComponents.states[i][j].getState() != 0) {  // state must be > 0 else stateColorMap indexError
                // draw square
                int state = layerComponents.getState(i, j);
                //            System.out.println("("+i+","+j+")"+"="+state);
                renderer.setColor(layerComponents.stateColorMap[state - 1]);

                float x = i * (layerComponents.cellSize + 1) + x_origin;  // +1 for cell border
                float y = j * (layerComponents.cellSize + 1) + y_origin;
                renderer.rect(x, y, layerComponents.cellSize, layerComponents.cellSize);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            // catch any out-of-bounds and ignore
            logger.error("ERR: colorMap out of bounds: ", ex);
        }
    }
}
