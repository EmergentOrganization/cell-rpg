package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.scenes.game.WorldScene;
import com.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory1;
import com.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory2;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion2 implements iRegion {
    WorldScene scene;
    public ArcadeRegion2(WorldScene parentScene){
        super();
        scene = parentScene;
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
        TagManager tagMan = world.getSystem(TagManager.class);
        setCAEdgeSpawns(tagMan);

        // setup the player-centric SpontGen
        Entity player = tagMan.getEntity(Tags.PLAYER);
        SpontaneousGenerationList genList = player.getComponent(SpontaneousGenerationList.class);
        genList.clear();
        genList.stampList.add(CGoLShapeConsts.GLIDER_DOWN_LEFT);
        genList.stampList.add(CGoLShapeConsts.GLIDER_DOWN_RIGHT);
        genList.stampList.add(CGoLShapeConsts.GLIDER_UP_RIGHT);
        genList.stampList.add(CGoLShapeConsts.GLIDER_UP_LEFT);
        genList.layers.add(CALayer.VYROIDS);
        genList.frequency = 1;

        // load story
        scene.dialogDisplay.loadDialogueSequence(new ArcadeStory2());
    }

    private void setCAEdgeSpawns(TagManager tagMan){
        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_50_50;
        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_50_50;
    }
}
