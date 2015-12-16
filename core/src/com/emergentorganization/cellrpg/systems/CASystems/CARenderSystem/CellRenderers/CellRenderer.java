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

    public static EnumMap<CellRenderer, ICellRenderer> getRendererMap(){
        // maps enums to interface implementers.
        EnumMap<CellRenderer, ICellRenderer> cellRenderer
                = new EnumMap<CellRenderer, ICellRenderer>(CellRenderer.class);

        cellRenderer.put(CellRenderer.COLOR_MAP, new ColorMapCellRenderer());
        cellRenderer.put(CellRenderer.DECAY, new DecayCellRenderer());
        cellRenderer.put(CellRenderer.GENETIC, new GeneticCellRenderer());

        return cellRenderer;
    }
}
