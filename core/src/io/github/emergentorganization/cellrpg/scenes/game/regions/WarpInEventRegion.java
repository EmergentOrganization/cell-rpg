package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.ApparitionCreator.ApparitionCreator;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;
import io.github.emergentorganization.emergent2dcore.systems.MoodSystem;
import io.github.emergentorganization.emergent2dcore.systems.RenderSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Region in which a given set of Entities and/or shapes warp in.
 * Region completed when enough of the warp-ins are destroyed or a timer runs out.
 */
public class WarpInEventRegion extends TimedRegion{

    public WorldScene scene;

    EntityID[] entityIDs;
    int[] entityCounts;
    int[][][] shapes;
    int[] shapeCounts;
    int minWarpDuration = 1*1000; // min time to warp-in [s]
    int maxWarpDuration = 5*1000;  // duration of warp-in (time across which warp-ins will start) [s]
    public int regionNumber = 0;
    EntityFactory entityFactory;

    private final Logger logger = LogManager.getLogger(getClass());

    public WarpInEventRegion(WorldScene parentScene, EntityFactory entityFactory, final long expiresIn){
        this(parentScene, entityFactory, expiresIn, new EntityID[]{}, new int[]{}, new int[][][]{}, new int[]{}, 0);
    }

    public WarpInEventRegion(WorldScene parentScene, EntityFactory entityFactory, final long expiresIn,
                             EntityID[] entityIDs, int[] entityCounts, int[][][] shapes, int[] shapeCounts,
                             int regionNumber) {
        super(expiresIn);

        assert entityIDs.length == entityCounts.length;
        assert shapes.length == shapeCounts.length;

        this.regionNumber = regionNumber;
        this.entityFactory = entityFactory;
        this.entityIDs = entityIDs;
        this.entityCounts = entityCounts;
        this.shapes = shapes;
        this.shapeCounts = shapeCounts;

        scene = parentScene;
    }

    public WarpInEventRegion(WorldScene parentScene, EntityFactory entityFactory, final long expiresIn,
                             EntityID[] entityIDs, int[] entityCounts, int[][][] shapes, int[] shapeCounts){
        this(parentScene, entityFactory, expiresIn, entityIDs, entityCounts, shapes, shapeCounts, 0);
    }

    public void loadRegion(World world) {
    }

    @Override
    public boolean readyForNextRegion(World world){
        boolean ret = super.readyForNextRegion(world);
        if (ret == true){
            return ret;
        } else {
            // check for ready for next region based on game mood
            int intensity = world.getSystem(MoodSystem.class).scoreIntensityLevelOutOf(10);
            if (intensity < 2){
                // if player has reduced mood intensity rating (by killing or running from enemies):
                return true;
            }
        }
        return false;
    }

    @Override
    protected iRegion _getNextRegion() {
       return getNextWarpIn();
    }

    @Override
    public void enterRegion(World world) {
        super.enterRegion(world);
        TagManager tagMan = world.getSystem(TagManager.class);
        Entity player = tagMan.getEntity(Tags.PLAYER);

        for (int idN = 0; idN < entityIDs.length; idN++){
            scheduleEntityWarps(player, entityIDs[idN], entityCounts[idN], world);
        }

        for (int shapeN = 0; shapeN < shapes.length; shapeN++){
//            scheduleShapeWarps(player, shapes[shapeN], shapeCounts[shapeN]);  // TODO
        }
    }

    private void scheduleEntityWarps(Entity target, EntityID entity, int amount, World world){
        logger.trace("warping in " + amount + " " + entity.toString() + "(s) in next " + maxWarpDuration + "s");
        for (int i = 0; i < amount; i++) {
            int delay = ApparitionCreator.getDelay(minWarpDuration, maxWarpDuration);

            ApparitionCreator.apparateGivenEntityIn(entity, delay, world.getSystem(AssetManager.class),
                                               world.getSystem(RenderSystem.class),
                                               target.getComponent(EntitySpawnField.class),
                                               target.getComponent(Position.class),
                                               target.getComponent(Bounds.class), entityFactory);
        }
    }

    private iRegion getNextWarpIn(){
        EntityID[] ents = new EntityID[]{};
        int[] entCounts = new int[]{};
        int[][][] shapes= new int[][][]{};
        int[] shapeCounts=new int[]{};

        switch (regionNumber){
            case 0:
                ents = new EntityID[]{EntityID.PONDBOMB};
                entCounts = new int[]{10};
                break;
            case 1:
                ents = new EntityID[]{EntityID.TUBSNAKE};
                entCounts = new int[]{3};
                shapes = new int[][][]{CGoLShapeConsts.BLINKER_H, CGoLShapeConsts.BLINKER_V};
                shapeCounts = new int[]{3, 3};
                break;
            case 2:
                ents = new EntityID[]{EntityID.PONDBOMB, EntityID.TUBSNAKE};
                entCounts = new int[]{10, 2};
                break;
            case 3:
                ents = new EntityID[]{EntityID.VYRAPUFFER};
                entCounts = new int[]{1};
                break;
            default:
                ents = new EntityID[]{EntityID.TUBSNAKE, EntityID.VYRAPUFFER};
                entCounts = new int[]{5, 3};
                shapes = new int[][][]{CGoLShapeConsts.R_PENTOMINO,
                        CGoLShapeConsts.GLIDER_DOWN_LEFT, CGoLShapeConsts.GLIDER_DOWN_RIGHT,
                        CGoLShapeConsts.GLIDER_UP_LEFT, CGoLShapeConsts.GLIDER_UP_RIGHT};
                shapeCounts = new int[]{5, 10, 10, 10, 10};
        }

        return new WarpInEventRegion(scene, entityFactory, maxLength, ents, entCounts, shapes, shapeCounts, regionNumber+1);
    }

}
