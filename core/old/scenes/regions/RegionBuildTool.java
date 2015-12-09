package com.emergentorganization.cellrpg.scenes.regions;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.entities.ca.*;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;

import java.util.Map;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class RegionBuildTool {

    public static void addVyroidLayer(Scene scene, Map<CALayer, CAGridBase> ca_layers, CALayer layerToAdd){
        ca_layers.put(layerToAdd, getDefaultCAGrid(layerToAdd));
        scene.addEntity(ca_layers.get(layerToAdd));
    }

    public static CAGridBase getDefaultCAGrid(CALayer layerType){
        Color[] colorMap = new Color[]{new Color(1f, .2f, .2f, 1f), new Color(1f, .4f, .8f, .8f)};
        int size = 3;
        switch (layerType){
            case VYROIDS_MINI:
                size = 1;
                colorMap = new Color[] {new Color(1f, .87f, .42f, 1f), new Color(1f, .4f, .8f, .8f)};
                return new BufferedCAGrid(size, ZIndex.VYROIDS, colorMap);

            case VYROIDS_MEGA:
                size = 11;
                colorMap = new Color[]{new Color(.2f, .2f, 1f, 1f), new Color(1f, .4f, .8f, .8f)};
                return new BufferedCAGrid(size, ZIndex.VYROIDS, colorMap);

            case VYROIDS:
                return new BufferedCAGrid(size, ZIndex.VYROIDS, colorMap);

            case VYROIDS_GENETIC:
                size = 11;
                return new GeneticCAGrid(size, ZIndex.VYROIDS);

            case ENERGY:
                size = 1;
                colorMap = new Color[] {new Color(1f, 1f, 1f, .8f)};
                return new decayCAGrid(size, ZIndex.VYROIDS, colorMap);

            default:
                return new NoBufferCAGrid(size, ZIndex.VYROIDS, colorMap);
        }
    }
}
