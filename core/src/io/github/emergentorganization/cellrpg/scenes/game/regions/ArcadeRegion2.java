package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Align;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldScene;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.SequentialStoryDialogue;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;


public class ArcadeRegion2 implements iRegion {
    private final WorldScene scene;

    public ArcadeRegion2(WorldScene parentScene) {
        super();
        scene = parentScene;
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
        tagMan.getEntity(CALayer.VYROIDS.getTag()).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_SPARSE;
        tagMan.getEntity(CALayer.VYROIDS_GENETIC.getTag()).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
    }
}
