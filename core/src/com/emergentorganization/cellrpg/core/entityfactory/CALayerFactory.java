package com.emergentorganization.cellrpg.core.entityfactory;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.CellType;
import com.emergentorganization.cellrpg.systems.CARenderSystem.layers.CALayer;

/**
 * Methods for building different types of CA layer entities by initializing the CAGridComponent.
 * Adapted from LayerBuilder (10-10-2015) on 12-12-2015 by 7yl4r.
 */
public class CALayerFactory {

    public static void initLayerComponentsByType(CAGridComponents layerComponents, CALayer layerType){
        // initializes given components such that they conform to the given type

        // start out with the default settings
        layerComponents.stateColorMap = new Color[]{new Color(1f, .2f, .2f, 1f), new Color(1f, .4f, .8f, .8f)};
        layerComponents.cellSize = 3;
        layerComponents.cellType = CellType.WITH_HISTORY;
        // TODO: layerComponents.renderType = CARenderType.BUFFERED;

        // and then overwrite defaults as required based on type
        switch (layerType){
            case VYROIDS_MINI:
                layerComponents.cellSize = 1;
                layerComponents.stateColorMap = new Color[] {new Color(1f, .87f, .42f, 1f), new Color(1f, .4f, .8f, .8f)};
                return;

            case VYROIDS_MEGA:
                layerComponents.cellSize = 11;
                layerComponents.stateColorMap = new Color[]{new Color(.2f, .2f, 1f, 1f), new Color(1f, .4f, .8f, .8f)};
                return;

            case VYROIDS:
                return;

            case VYROIDS_GENETIC:
                layerComponents.cellSize = 11;
                // TODO: layerComponents.renderType = CARenderType.GENETIC;
                // TODO: layerComponents.cellType = CellType.GENETIC;
                return;

            case ENERGY:
                layerComponents.cellSize = 1;
                layerComponents.stateColorMap = new Color[] {new Color(1f, 1f, 1f, .8f)};
                // TODO: layerComponents.renderType = CARenderType.DECAY;
                layerComponents.cellType = CellType.BASE;
                return;
        }
    }
}
