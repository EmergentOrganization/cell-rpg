package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.CollectibleSpawnField;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.components.StatsTracker;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion1 implements iRegion {
    public ArcadeRegion1(){
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
        // if score is high enough return next region
        TagManager tagMan = world.getSystem(TagManager.class);
        final int score = tagMan.getEntity(Tags.PLAYER).getComponent(StatsTracker.class).getScore();
        final int SCORE_TO_MOVE_ON = 1000;
        if (score > SCORE_TO_MOVE_ON){
            return new ArcadeRegion2();
        } else {
            return null;
        }
    }

    public void loadRegion(World world){
    }

    public void enterRegion(World world){
        System.out.println("entering arcade region 1");
        TagManager tagMan = world.getSystem(TagManager.class);

        setCAEdgeSpawns(tagMan);

        Entity player = tagMan.getEntity(Tags.PLAYER);

        // setup the player-centric SpontGen
        SpontaneousGenerationList genList = player.getComponent(SpontaneousGenerationList.class);
        genList.clear();
        genList.stampList.add(CGoLShapeConsts.GLIDER_DOWN_LEFT);
        genList.stampList.add(CGoLShapeConsts.GLIDER_DOWN_RIGHT);
        genList.stampList.add(CGoLShapeConsts.GLIDER_UP_RIGHT);
        genList.stampList.add(CGoLShapeConsts.GLIDER_UP_LEFT);
        genList.layers.add(CALayer.VYROIDS);
        genList.frequency = 10;

        // setup power-up spawns around player
        CollectibleSpawnField spawnField = player.getComponent(CollectibleSpawnField.class);
        spawnField.entityList.clear();
        spawnField.entityList.add(EntityID.BULLET);  // TODO: use power-up instead of bullet
        spawnField.frequency = 20;
    }

    private void setCAEdgeSpawns(TagManager tagMan){

        Entity CAEnt = tagMan.getEntity(Tags.CA_VYROIDS_STD);
        CAGridComponents CAComp = CAEnt.getComponent(CAGridComponents.class);
        CAComp.edgeSpawner = CAEdgeSpawnType.RANDOM_SPARSE;

        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
    }
}
