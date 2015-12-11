//package com.emergentorganization.cellrpg.systems.CARenderSystem.layers;
//
//import com.badlogic.gdx.graphics.Color;
//import com.emergentorganization.cellrpg.systems.CARenderSystem.CAGrid.*;
//
//import java.util.Map;
//
///**
// * Created by 7yl4r on 10/10/2015.
// */
//public class LayerBuilder {
//
//    public static CAGridBase addVyroidLayer(Map<CALayer, CAGridBase> ca_layers, CALayer layerToAdd){
//        ca_layers.put(layerToAdd, getDefaultCAGrid(layerToAdd));
//        return ca_layers.get(layerToAdd);
//    }
//
//    public static CAGridBase getDefaultCAGrid(CALayer layerType){
//        Color[] colorMap = new Color[]{new Color(1f, .2f, .2f, 1f), new Color(1f, .4f, .8f, .8f)};
//        int size = 3;
//        switch (layerType){
//            case VYROIDS_MINI:
//                size = 1;
//                colorMap = new Color[] {new Color(1f, .87f, .42f, 1f), new Color(1f, .4f, .8f, .8f)};
//                return new BufferedCAGrid(size, colorMap);
//
//            case VYROIDS_MEGA:
//                size = 11;
//                colorMap = new Color[]{new Color(.2f, .2f, 1f, 1f), new Color(1f, .4f, .8f, .8f)};
//                return new BufferedCAGrid(size, colorMap);
//
//            case VYROIDS:
//                return new BufferedCAGrid(size, colorMap);
//
//            case VYROIDS_GENETIC:
//                size = 11;
//                return new GeneticCAGrid(size);
//
//            case ENERGY:
//                size = 1;
//                colorMap = new Color[] {new Color(1f, 1f, 1f, .8f)};
//                return new decayCAGrid(size, colorMap);
//
//            default:
//                return new BufferedCAGrid(size, colorMap);//NoBufferCAGrid(size, colorMap);
//        }
//    }
//}
