package com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers;

import java.util.EnumMap;

/**
 * Cell renderers which can be used by CALayerComponents.renderType.
 *
 * Created by 7yl4r on 12/14/2015.
 */
public enum CellRenderer {
    COLOR_MAP,
    DECAY,
    GENETIC;

    public static EnumMap<CellRenderer, iCellRenderer> getRendererMap(){
        // maps enums to interface implementers.
        EnumMap<CellRenderer, iCellRenderer> cellRenderer
                = new EnumMap<CellRenderer, iCellRenderer>(CellRenderer.class);

        cellRenderer.put(CellRenderer.COLOR_MAP, new ColorMapCellRenderer());
        cellRenderer.put(CellRenderer.DECAY, new DecayCellRenderer());
        cellRenderer.put(CellRenderer.GENETIC, new GeneticCellRenderer());

        return cellRenderer;
    }
}
