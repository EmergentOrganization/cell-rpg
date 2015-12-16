package com.emergentorganization.cellrpg.core.entityfactory;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.CellType;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.CellRenderer;
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CA;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Methods for building different types of CA layer entities by initializing the CAGridComponent.
 * Adapted from LayerBuilder (10-10-2015) on 12-12-2015 by 7yl4r.
 *
 * TODO: this should be part of the CALayerComponentsConstructor, not a separate class.
 */
public class CALayerFactory {

    public static void initLayerComponentsByType(CAGridComponents layerComponents, CALayer layerType){
        // initializes given components such that they conform to the given type

        // start out with the default settings
        layerComponents.stateColorMap = new Color[]{new Color(1f, .2f, .2f, 1f), new Color(1f, .4f, .8f, .8f)};
        layerComponents.cellSize = 3;
        layerComponents.cellType = CellType.WITH_HISTORY;
        layerComponents.renderType = CellRenderer.COLOR_MAP;
        layerComponents.edgeSpawner = CAEdgeSpawnType.RANDOM_SPARSE;
        layerComponents.ca = CA.BUFFERED;

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
                layerComponents.cellSize = 11;//35
                layerComponents.renderType = CellRenderer.GENETIC;
                layerComponents.cellType = CellType.GENETIC;
                layerComponents.ca = CA.GENETIC;
                return;

            case ENERGY:
                layerComponents.TIME_BTWN_GENERATIONS = 700;
                layerComponents.cellSize = 3;
                layerComponents.stateColorMap = new Color[] {new Color(1f, 1f, 1f, .8f)};
                layerComponents.renderType = CellRenderer.DECAY;
                layerComponents.cellType = CellType.DECAY;
                layerComponents.ca = CA.DECAY;
                return;
        }
    }
}
