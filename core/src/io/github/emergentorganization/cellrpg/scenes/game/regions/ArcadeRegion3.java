package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Align;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.SequentialStoryDialogue;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;


public class ArcadeRegion3 implements iRegion {
    WorldScene scene;

    public ArcadeRegion3(WorldScene parentScene) {
        super();
        scene = parentScene;
    }

    public iRegion getNextRegion(World world) {
        return null;
    }

    public void loadRegion(World world) {
    }

    public void enterRegion(World world) {
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
        genList.stampList.add(CGoLShapeConsts.R_PENTOMINO);
        genList.layers.add(CALayer.VYROIDS);
        genList.frequency = 2;
        genList.layers.add(CALayer.VYROIDS_GENETIC);

        // load story
        scene.dialogDisplay.loadDialogueSequence(new SequentialStoryDialogue(ArcadeStory.III), Align.topLeft);
    }

    private void setCAEdgeSpawns(TagManager tagMan) {
        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_SPARSE;
        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_SPARSE;
    }
}
