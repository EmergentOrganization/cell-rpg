package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.World;
import com.artemis.managers.TagManager;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.StatsTracker;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion2 implements iRegion {
    public ArcadeRegion2(){
        super();
    }

    public CALayer[] getCALayers(){
        // TODO: this is currently unused, but layers should be dynamically added/removed
        // TODO:    by a CA Manager.
        return new CALayer[]{
                CALayer.ENERGY,
                CALayer.VYROIDS,
                CALayer.VYROIDS_MINI,
                CALayer.VYROIDS_MEGA
        };
    }

    public iRegion getNextRegion(World world){
        return null;
    }

    public void loadRegion(World world){
    }

    public void enterRegion(World world){
        System.out.println("entering arcade region 2");
        setCAEdgeSpawns(world);
    }

    private void setCAEdgeSpawns(World world){
        TagManager tagMan = world.getSystem(TagManager.class);
        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_50_50;
        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_50_50;
    }
}
