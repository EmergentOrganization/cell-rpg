package io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;


public interface iCellRenderer {
    /**
     * renders the cell at i, j in layarComponents (x_origin and y_origin provided so camera is not needed)
     */
    void renderCell(ShapeRenderer renderer, CAGridComponents layerComponents, final int i, final int j,
                    final float x_origin, final float y_origin);
}
