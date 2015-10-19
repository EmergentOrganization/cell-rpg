package com.emergentorganization.cellrpg.scenes.regions;

import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion1 extends Region {
    private final long REGION_LENGTH = 3000;  // amount of time required to spend in this region

    private long regionTime;

    public ArcadeRegion1(Scene parentScene){
        super(parentScene);
        regionTime = TimeUtils.millis();
    }

    @Override
    public void addCALayers(){
        super.addCALayers();
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS);
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS_MINI);
        RegionBuildTool.addVyroidLayer(scene, ca_layers, CALayer.VYROIDS_MEGA);
    }

    public boolean regionFinished(){
        // this is a timeout region, it will finish if player survives for REGION_LENGTH ms.
        return (TimeUtils.millis() - regionTime) > REGION_LENGTH;
    }
}
