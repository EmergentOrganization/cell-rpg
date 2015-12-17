package com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Created by 7yl4r on 12/14/2015.
 */
public interface iCellRenderer {
    void renderCell(ShapeRenderer renderer, CAGridComponents layerComponents, final int i, final int j,
                           final float x_origin, final float y_origin);
        // renders the cell at i, j in layarComponents (x_origin and y_origin provided so camera is not needed)
}
