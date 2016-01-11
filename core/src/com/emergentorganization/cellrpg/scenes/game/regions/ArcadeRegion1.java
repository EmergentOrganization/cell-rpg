package com.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.CollectibleSpawnField;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import com.emergentorganization.cellrpg.components.StatsTracker;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.RenderIndex;
import com.emergentorganization.cellrpg.core.Tags;
import com.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
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
 * Created by 7yl4r on 10/10/2015.
 */
public class ArcadeRegion1 implements iRegion {
    WorldScene scene;
    public ArcadeRegion1(WorldScene parentScene){
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
        // if score is high enough return next region
        TagManager tagMan = world.getSystem(TagManager.class);
        final int score = tagMan.getEntity(Tags.PLAYER).getComponent(StatsTracker.class).getScore();
        final int SCORE_TO_MOVE_ON = 1000;
        if (score > SCORE_TO_MOVE_ON){
            return new ArcadeRegion2(scene);
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
        genList.frequency = 8;

        // setup power-up spawns around player
        CollectibleSpawnField spawnField = player.getComponent(CollectibleSpawnField.class);
        spawnField.entityList.clear();
        spawnField.entityList.add(EntityID.POWERUP_PLUS);
        spawnField.entityList.add(EntityID.POWERUP_STAR);
        spawnField.frequency = -1;

        // load story
        scene.dialogDisplay.loadDialogueSequence(new SequentialStoryDialogue(ArcadeStory.I));

        Entity bg = new EntityBuilder(
                world,
                EntityFactory.object,
                "Arcade Background",
                EntityID.BG_ARCADE.toString(),
                player.getComponent(Position.class).position.cpy().sub(2000*.025f,2000*.025f)  // minus 1/2 texture size
        )
                .addBuilder(new VisualBuilder()
                        .texture(Resources.TEX_BG_ARCADE)
                        .renderIndex(RenderIndex.BACKGROUND)
                )
                .build();
    }

    private void setCAEdgeSpawns(TagManager tagMan){

        tagMan.getEntity(Tags.CA_VYROIDS_STD).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;

        tagMan.getEntity(Tags.CA_VYROIDS_GENETIC).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.EMPTY;
    }
}
