package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Align;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.CollectibleSpawnField;
import io.github.emergentorganization.engine.components.Position;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory;
import io.github.emergentorganization.cellrpg.scenes.game.dialogue.SequentialStoryDialogue;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.Resources;


public class ArcadeRegion1 implements iRegion {
    WorldScene scene;

    public ArcadeRegion1(WorldScene parentScene) {
        super();
        scene = parentScene;
    }

    public iRegion getNextRegion(World world) {
        // if score is high enough return next region
        TagManager tagMan = world.getSystem(TagManager.class);
        final int score = tagMan.getEntity(Tags.PLAYER).getComponent(StatsTracker.class).getScore();
        final int SCORE_TO_MOVE_ON = 1000;
        if (score > SCORE_TO_MOVE_ON) {
            return new ArcadeRegion2(scene);
        } else {
            return null;
        }
    }

    public void loadRegion(World world) {
    }

    public void enterRegion(World world) {
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
        genList.frequency = 8;

        // setup power-up spawns around player
        CollectibleSpawnField spawnField = player.getComponent(CollectibleSpawnField.class);
        spawnField.entityList.clear();
        spawnField.entityList.add(EntityID.POWERUP_PLUS);
        spawnField.entityList.add(EntityID.POWERUP_STAR);
        spawnField.frequency = -1;

        // load story
        scene.dialogDisplay.loadDialogueSequence(new SequentialStoryDialogue(ArcadeStory.I), Align.topLeft);

        Entity bg = new EntityBuilder(
                world,
                EntityFactory.object,
                "Arcade Background",
                EntityID.BG_ARCADE.toString(),
                player.getComponent(Position.class).position.cpy().sub(2000 * .025f, 2000 * .025f)  // minus 1/2 texture size
        )
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_BG_ARCADE)
                        .renderIndex(RenderIndex.BACKGROUND)
                )
                .build();
    }

    private void setCAEdgeSpawns(TagManager tagMan) {

        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;

        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
    }
}
