package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.CollectibleSpawnField;
import io.github.emergentorganization.engine.components.Position;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.components.StatsTracker;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import com.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.LifecycleBuilder;
import com.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder.VisualBuilder;
import com.emergentorganization.cellrpg.scenes.game.WorldScene;
import com.emergentorganization.cellrpg.scenes.game.dialogue.ArcadeStory;
import com.emergentorganization.cellrpg.scenes.game.dialogue.SequentialStoryDialogue;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import com.emergentorganization.cellrpg.tools.Resources;

/**
 * region to create score-based difficulty ramp for arcade mode.
 * Region never returns a new region to swtich to, instead, it uses `getNextRegion`
 * as an opportunity to modify this region.
 */
public class ArcadeRegion implements iRegion {
    private static int SCL = 100;  // use this to scale up/down all score thresholds to adjust difficulty ramp
    WorldScene scene;

    public ArcadeRegion(WorldScene parentScene) {
        super();
        scene = parentScene;
    }

    public iRegion getNextRegion(World world) {
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        updateRegion(player, tagMan);

        return null;  // never leave this region
    }

    public void loadRegion(World world) {
    }

    public void enterRegion(World world) {
        System.out.println("entering arcade region");
        TagManager tagMan = world.getSystem(TagManager.class);

        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;

        Entity player = tagMan.getEntity(Tags.PLAYER);
        // setup the player-centric SpontGen
        player.getComponent(SpontaneousGenerationList.class).clear();

        // setup power-up spawns around player
        player.getComponent(CollectibleSpawnField.class).entityList.clear();

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
                .addBuilder(new LifecycleBuilder(-1))
                .build();
    }

    private void updateRegion(Entity player, TagManager tagMan) {
        int score = player.getComponent(StatsTracker.class).getScore();

        adjustPowerups(score, player);
        adjustCASpawns(score, player);
        adjustCABoundaries(score, tagMan);
    }

    private int getPowerupFreq(int score) {
        return 99 / (500 * SCL) * score + 1;
    }

    private void adjustPowerups(int score, Entity player) {
        CollectibleSpawnField spawnField = player.getComponent(CollectibleSpawnField.class);
        spawnField.frequency = getPowerupFreq(score);
        if (score > 20 * SCL) {
            spawnField.entityList.add(EntityID.VYRAPUFFER);
        } else if (score > 10 * SCL) {
            spawnField.entityList.add(EntityID.POWERUP_PLUS);
        } else if (score > 5 * SCL) {
            spawnField.entityList.add(EntityID.TUBSNAKE);
        } else if (score > 1 * SCL) {
            spawnField.entityList.add(EntityID.POWERUP_STAR);
        }
    }

    private int getCASpawnFreq(int score) {
        int res = -score * 8 / (200 * SCL) + 6;
        if (res < 1) {
            return 1;  // no lower than 1
        } else {
            return res;
        }
    }

    private void setCASpawnLayers(SpontaneousGenerationList genList, int score) {
        if (score > 100 * SCL) {
            // both (b/c default vyroid remains in list uncleared)
            genList.layers.add(CALayer.VYROIDS_GENETIC);
        } else if (score > 10 * SCL) {
            genList.clear();
            genList.layers.add(CALayer.VYROIDS);
        } else if (score > -1) {
            genList.layers.add(CALayer.VYROIDS_GENETIC);
        }
    }

    private void adjustCASpawns(int score, Entity player) {
        SpontaneousGenerationList genList = player.getComponent(SpontaneousGenerationList.class);
        genList.frequency = getCASpawnFreq(score);
        setCASpawnLayers(genList, score);
        if (score > 1000 * SCL) {
            genList.stampList.clear();  // clear out the easy stuff
            // put the hard stuff back
            genList.stampList.add(CGoLShapeConsts.R_PENTOMINO);
        } else if (score > 100 * SCL) {
            genList.stampList.add(CGoLShapeConsts.R_PENTOMINO);
        } else if (score > 50 * SCL) {
            genList.stampList.add(CGoLShapeConsts.GLIDER_DOWN_LEFT);
            genList.stampList.add(CGoLShapeConsts.GLIDER_DOWN_RIGHT);
            genList.stampList.add(CGoLShapeConsts.GLIDER_UP_RIGHT);
        } else if (score > 10 * SCL) {
            genList.stampList.add(CGoLShapeConsts.GLIDER_UP_LEFT);
        } else if (score > 5 * SCL) {
            genList.stampList.add(CGoLShapeConsts.BLINKER_H);
            genList.stampList.add(CGoLShapeConsts.BLINKER_V);
        } else if (score > 0) {
            genList.stampList.add(CGoLShapeConsts.BLOCK);
        }
    }

    private void adjustCABoundaries(int score, TagManager tagMan) {
        CAGridComponents stdCA = tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class);
        CAGridComponents geneticCA = tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class);
        if (score > 1000 * SCL) {
            stdCA.edgeSpawner = CAEdgeSpawnType.RANDOM_DENSE;
            geneticCA.edgeSpawner = CAEdgeSpawnType.RANDOM_DENSE;
        } else if (score > 100 * SCL) {
            stdCA.edgeSpawner = CAEdgeSpawnType.RANDOM_50_50;
            geneticCA.edgeSpawner = CAEdgeSpawnType.RANDOM_50_50;
        } else if (score > 50 * SCL) {
            stdCA.edgeSpawner = CAEdgeSpawnType.RANDOM_SPARSE;
        } else if (score > 10 * SCL) {
            geneticCA.edgeSpawner = CAEdgeSpawnType.RANDOM_SPARSE;
        } else if (score > -1) {
            stdCA.edgeSpawner = CAEdgeSpawnType.EMPTY;
            geneticCA.edgeSpawner = CAEdgeSpawnType.EMPTY;
        }
    }
}
