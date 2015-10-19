package com.emergentorganization.cellrpg.scenes.regions;
import com.emergentorganization.cellrpg.entities.ca.CAGridBase;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;

import java.util.EnumMap;
import java.util.Map;

/**
 * Base class used to create an area (aka region chunk) within a scene.
 * Useful to allow switching between "region" units without breaking continuity of the scene,
 * such as moving to next arcade level or loading next area chunk while traveling.
 *
 * Created by 7yl4r on 2015-10-10.
 */
public abstract class Region {
    protected Map<CALayer, CAGridBase> ca_layers = new EnumMap<CALayer, CAGridBase>(CALayer.class);
    protected Scene scene;

    public Region(Scene parentScene){
        scene = parentScene;
    }

    public void addCALayers(){
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.ENERGY);
    }

    public void removeCALayers(){
        for (CAGridBase layer : ca_layers.values()){
            scene.removeEntity(layer);
        }
    }

    public CAGridBase getLayer(CALayer layer){
        return ca_layers.get(layer);
    }

    public abstract boolean regionFinished();
    // returns true when region win condition is satisfied or player is leaving region.
    // use this to signal to the parent scene that it should prepare the next region.
}
