package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.components.StatsTracker;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.scenes.game.WorldScene;
import com.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory;
import com.emergentorganization.cellrpg.scenes.game.dialogue.SequentialStoryDialogue;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;


public class ArcadeRegion2 implements iRegion {
    WorldScene scene;

    public ArcadeRegion2(WorldScene parentScene) {
        super();
        scene = parentScene;
    }

    public CALayer[] getCALayers() {
        // TODO: this is currently unused, but layers should be dynamically added/removed
        // TODO:    by a CA Manager.
        return new CALayer[]{
                CALayer.ENERGY,
                CALayer.VYROIDS,
                CALayer.VYROIDS_MINI,
                CALayer.VYROIDS_MEGA
        };
    }

    public iRegion getNextRegion(World world) {
        // if score is high enough return next region
        TagManager tagMan = world.getSystem(TagManager.class);
        final int score = tagMan.getEntity(Tags.PLAYER).getComponent(StatsTracker.class).getScore();
        final int SCORE_TO_MOVE_ON = 10000;
        if (score > SCORE_TO_MOVE_ON) {
            return new ArcadeRegion3(scene);
        } else {
            return null;
        }
    }

    public void loadRegion(World world) {
    }

    public void enterRegion(World world) {
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
        genList.frequency = 4;
        genList.layers.add(CALayer.VYROIDS_GENETIC);

        // load story
        scene.dialogDisplay.loadDialogueSequence(new SequentialStoryDialogue(ArcadeStory.II), Align.topLeft);
    }

    private void setCAEdgeSpawns(TagManager tagMan) {
        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_SPARSE;
        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
    }
}
