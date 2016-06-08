package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.systems.MoodSystem;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldScene;
import io.github.emergentorganization.cellrpg.systems.SpawningSystem;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Region in which a given set of Entities and/or shapes warp in.
 * Region completed when enough of the warp-ins are destroyed or a timer runs out.
 */
public class WarpInEventRegion extends TimedRegion {

    private final Logger logger = LogManager.getLogger(getClass());
    private final SpawningSystem spawningSystem;
    public int regionNumber = 0;
    private EntityID[] entityIDs;
    private int[] entityCounts;
    private int[][][] shapes;
    private int[] shapeCounts;
    private final int minWarpDuration = 1 * 1000; // min time to warp-in [s]
    private final int maxWarpDuration = 5 * 1000;  // duration of warp-in (time across which warp-ins will start) [s]
    private EntityFactory entityFactory;

    public WarpInEventRegion(EntityFactory entityFactory, final long expiresIn,
                             int regionNumber, SpawningSystem spawningSystem) {
        this(entityFactory, expiresIn, new EntityID[]{}, new int[]{}, new int[][][]{}, new int[]{},
                regionNumber, spawningSystem);
    }

    public WarpInEventRegion(EntityFactory entityFactory, final long expiresIn, SpawningSystem spawningSystem) {
        this(entityFactory, expiresIn, 0, spawningSystem);
    }

    private WarpInEventRegion(EntityFactory entityFactory, final long expiresIn,
                              EntityID[] entityIDs, int[] entityCounts, int[][][] shapes, int[] shapeCounts,
                              int regionNumber, SpawningSystem spawningSystem) {
        super(expiresIn);

        assert entityIDs.length == entityCounts.length;
        assert shapes.length == shapeCounts.length;

        this.regionNumber = regionNumber;
        this.entityFactory = entityFactory;
        this.entityIDs = entityIDs;
        this.entityCounts = entityCounts;
        this.shapes = shapes;
        this.shapeCounts = shapeCounts;
        this.spawningSystem = spawningSystem;
    }

    public WarpInEventRegion(EntityFactory entityFactory, final long expiresIn,
                             EntityID[] entityIDs, int[] entityCounts, int[][][] shapes, int[] shapeCounts, SpawningSystem spawningSystem) {
        this(entityFactory, expiresIn, entityIDs, entityCounts, shapes, shapeCounts, 0, spawningSystem);
    }

    public void loadRegion(World world) {
    }

    @Override
    public boolean readyForNextRegion(World world) {
        boolean ret = super.readyForNextRegion(world);
        if (ret == true) {
            return ret;
        } else {
            // check for ready for next region based on game mood
            int intensity = world.getSystem(MoodSystem.class).scoreIntensityLevelOutOf(10);
            if (intensity < 2) {
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

        for (int idN = 0; idN < entityIDs.length; idN++) {
            scheduleEntityWarps(player, entityIDs[idN], entityCounts[idN], world);
        }

        for (int[][] shape : shapes) {
//            scheduleShapeWarps(player, shapes[shapeN], shapeCounts[shapeN]);  // TODO
        }
    }

    private void scheduleEntityWarps(Entity target, EntityID entity, int amount, World world) {
        logger.trace("warping in " + amount + " " + entity.toString() + "(s) in next " + maxWarpDuration + "s");
        for (int i = 0; i < amount; i++) {
            long delay = (long) Math.min(minWarpDuration, Math.random() * maxWarpDuration);

            spawningSystem.spawnEntity(entity, delay, target.getComponent(Position.class),
                    target.getComponent(Bounds.class), target.getComponent(EntitySpawnField.class));
        }
    }

    private iRegion getNextWarpIn() {
        EntityID[] ents = new EntityID[]{};
        int[] entCounts = new int[]{};
        int[][][] shapes = new int[][][]{};
        int[] shapeCounts = new int[]{};

        final int N_REGION_TYPES = 10;  // # of cases (including default) in switch/case below (hightest # + 1)
        int regionType = regionNumber % N_REGION_TYPES;
        int multipler = regionNumber / N_REGION_TYPES + 1;

        switch (regionType) {
            case -1:  // test region with one of each entity; 0 is typical starting wave
                ents = new EntityID[]{EntityID.PONDBOMB, EntityID.TUBSNAKE, EntityID.VYRAPUFFER, EntityID.GOSPER};
                entCounts = new int[]{1, 1, 1, 1};
                break;
            case 0:
                shapes = new int[][][]{CGoLShapeConsts.BLINKER_H, CGoLShapeConsts.BLINKER_V};
                shapeCounts = new int[]{3, 3};
                break;
            case 1:
                ents = new EntityID[]{EntityID.PONDBOMB};
                entCounts = new int[]{3};
                break;
            case 2:
                shapes = new int[][][]{
                        CGoLShapeConsts.GLIDER_DOWN_RIGHT,
                        CGoLShapeConsts.GLIDER_UP_RIGHT,
                        CGoLShapeConsts.GLIDER_DOWN_LEFT,
                        CGoLShapeConsts.GLIDER_UP_LEFT
                };
                shapeCounts = new int[]{2, 2, 2, 2};
                break;
            case 3:
                ents = new EntityID[]{EntityID.TUBSNAKE};
                entCounts = new int[]{3};
                break;
            case 4:
                shapes = new int[][][]{CGoLShapeConsts.R_PENTOMINO};
                shapeCounts = new int[]{5};
                break;
            case 5:
                ents = new EntityID[]{EntityID.VYRAPUFFER};
                entCounts = new int[]{1};
                break;
            case 6:
                ents = new EntityID[]{EntityID.PONDBOMB, EntityID.TUBSNAKE};
                entCounts = new int[]{10, 2};
                break;
            case 7:
                shapes = new int[][][]{
                        CGoLShapeConsts.GOSPER_DOWN_LEFT,
                        CGoLShapeConsts.GOSPER_DOWN_RIGHT,
                        CGoLShapeConsts.R_PENTOMINO
                };
                shapeCounts = new int[]{2, 2, 4};
                break;
            case 8:
                ents = new EntityID[]{EntityID.GOSPER};
                entCounts = new int[]{1};
                break;
            default:
                ents = new EntityID[]{EntityID.TUBSNAKE, EntityID.VYRAPUFFER, EntityID.GOSPER};
                entCounts = new int[]{5, 1, 1};
                shapes = new int[][][]{CGoLShapeConsts.R_PENTOMINO,
                        CGoLShapeConsts.GLIDER_DOWN_LEFT, CGoLShapeConsts.GLIDER_DOWN_RIGHT,
                        CGoLShapeConsts.GLIDER_UP_LEFT, CGoLShapeConsts.GLIDER_UP_RIGHT};
                shapeCounts = new int[]{5, 10, 10, 10, 10};
        }

        // scale entCounts & shapeCounts using multiplier
        for (int i = 0; i < entCounts.length; i++) {
            entCounts[i] = entCounts[i] * multipler;
        }
        for (int i = 0; i < shapeCounts.length; i++) {
            shapeCounts[i] = shapeCounts[i] * multipler;
        }
        return new WarpInEventRegion(entityFactory, maxLength, ents, entCounts, shapes, shapeCounts, regionNumber + 1, spawningSystem);
    }

}
