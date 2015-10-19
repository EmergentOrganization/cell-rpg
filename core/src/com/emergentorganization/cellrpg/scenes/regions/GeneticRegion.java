package com.emergentorganization.cellrpg.scenes.regions;

import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class GeneticRegion extends Region {
    public GeneticRegion(Scene parentScene){
        super(parentScene);
    }

    @Override
    public void addCALayers(){
        super.addCALayers();
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS_GENETIC);
    }

    @Override
    public boolean regionFinished(){
        return false;  // this region never ends! Muahahaha!
    }
}
