package com.emergentorganization.cellrpg.scenes.regions;

import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class TheEdge extends Region {

    public TheEdge(Scene parentScene){
        super(parentScene);
    }

    @Override
    public void addCALayers(){
        super.addCALayers();
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS);
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS_MINI);
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS_MEGA);
    }

    @Override
    public boolean regionFinished(){
        return false;
        // TODO: for story mode, region should return true when player is nearing edge of the
        //          region and the next region chunk needs to be loaded.
    }
}
