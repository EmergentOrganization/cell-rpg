package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Profile;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.ParticleEff;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.systems.RenderSystem;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * System for warping in entities to appear in the game.<br>
 * Why?<br>
 * This should help add pre-warp particle effects and sounds.<br>
 * It may also be useful to allow us to time warp-ins to sync<br>
 * up with musical queues.
 */
@Profile(using = EmergentProfiler.class, enabled = true)
public class SpawningSystem extends BaseEntitySystem {
    private static final Logger logger = LogManager.getLogger(SpawningSystem.class);
    private final ArrayList<Runnable> tasks = new ArrayList<Runnable>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private AssetManager assetManager;
    private RenderSystem renderSystem;
    private SpontaneousGeneration spontGen;

    private ComponentMapper<Position> positionMap;
    private ComponentMapper<Bounds> boundsMap;
    private ComponentMapper<EntitySpawnField> spawnFieldMap;

    @Wire
    private EntityFactory entityFactory;

    public SpawningSystem() {
        super(Aspect.all(Position.class, Bounds.class, EntitySpawnField.class));
    }

    @Override
    protected void processSystem() {
        // Run currently queued tasks
        synchronized (tasks) {
            for (Runnable task : tasks) {
                task.run();
            }
            tasks.clear();
        }

        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            process(ids[i]);
        }
    }

    protected void process(int entityId) {
        TagManager tagMan = world.getSystem(TagManager.class);
        EntitySpawnField spawnField = spawnFieldMap.get(entityId);
        EntityID spawnable = spawnField.getSpawnableEntity();
        if (spawnable != null) {
            // spawns entity in the field.
            Position pos = positionMap.get(entityId);
            Bounds bounds = boundsMap.get(entityId);
            spawnEntity(spawnable, getDelay(), pos, bounds, spawnField);
        }
    }

    private void runLater(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    public void SpawnCAEffect(final CAGridComponents targetGrid) {
        initWarpInEffects(spontGen.position, getDelay());
        synchronized (executorService) {
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    runLater(new Runnable() {
                        @Override
                        public void run() {
                            targetGrid.stampCenteredAt(spontGen.stamp, spontGen.position);
                        }
                    });
                }
            }, getDelay(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Spawns given entity after a given delay time
     * @param entity The Entity type to spawn
     * @param delay The delay after which the entity spawns
     * @param pos Position of the source entity?
     * @param bounds Bounds of the source entity?
     * @param spawnField The spawnField of the source entity
     */
    public void spawnEntity(final EntityID entity, long delay, final Position pos, Bounds bounds, final EntitySpawnField spawnField) {
        final Vector2 spawnPos = spawnField.getSpawnPosition(pos, bounds);
        initWarpInEffects(spawnPos, delay);
        synchronized (executorService) {
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.debug("spawn ent " + entity + " @" + spawnPos + ")");
                            spawnField.spawnEntity(entity, entityFactory, spawnPos);
                        }
                    });
                }
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Initializes the warp-in particle effect(s) and sound(s) for a CA effect or entity
     * @param pos position of the warp-in
     * @param duration milliseconds until warp-in complete
     */
    private void initWarpInEffects(Vector2 pos, long duration) {
        ParticleEffect particleEffect = assetManager.getParticleEffect(ParticleEff.PREWARP);
        particleEffect.setPosition(pos.x, pos.y);
        // NOTE: the following assumes that the first emitter in the particle effect is the longest:
        particleEffect.setDuration(
                (int) (duration - particleEffect.getEmitters().get(0).duration)  // adjust for max particle lifespan
        );
//        particleEffect.allowCompletion();  // optional? (maybe only needed if continuous==true)
        particleEffect.start();
        renderSystem.registerOrphanParticleEffect(particleEffect);
        logger.debug("added new particle len= " + duration + " @ (" + pos.x + ',' + pos.y + ")");
    }

    private static int getDelay() {
        return getDelay(2000, 10000);
    }

    public static int getDelay(final int min, final int max) {
        // TODO: get delay from TimingSystem (which should be broken out from MusicSystem)
        return (int) Math.max(min, Math.floor(Math.random() * max));
    }

    @Override
    protected void dispose() {
        super.dispose();
        try {
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
            executorService.shutdownNow();
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }
}
