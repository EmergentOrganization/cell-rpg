package io.github.emergentorganization.cellrpg.tools.ApparitionCreator;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Timer;

/**
 * Static methods for warping in entities to appear in the game.
 * Why?
 * This should help add pre-warp particle effects and sounds.
 * It may also be useful to allow us to time warp-ins to sync
 * up with musical queues.
 * <p>
 * PS: it's called ApparitionCreator because it helps things appear...
 * ...Someone should proabably come up with a better name.
 */
public class ApparitionCreator {
    private static final Logger logger = LogManager.getLogger(ApparitionCreator.class);

    private static final Random rand = new Random();

    public static void apparateCAEffect(AssetManager assetMan, RenderSystem renderSys,
                                        SpontaneousGeneration spontGen, CAGridComponents targetGrid) {
        int delay = getDelay();
        initWarpInEffects(assetMan, renderSys, spontGen.position, delay);
        Timer time = new Timer();
        time.schedule(new CAApparitionTask(spontGen, targetGrid), delay);
    }

    public static void apparateEntity(AssetManager assetManage, RenderSystem rendSys,
                                      EntitySpawnField spawnField, Position pos, Bounds bound, EntityFactory ef) {
        apparateGivenEntityIn(spawnField._getSpawnableEntity(), getDelay(), assetManage, rendSys, spawnField, pos, bound, ef);
    }

    public static void apparateGivenEntityIn(EntityID entity, int delay,
                                             AssetManager assetManage, RenderSystem rendSys,
                                             EntitySpawnField spawnField, Position pos, Bounds bound, EntityFactory ef) {
        // apparates given entity after a given delay time
        Vector2 spawnPos = spawnField.getSpawnPosition(pos, bound);
        initWarpInEffects(assetManage, rendSys, spawnPos, delay);
        Timer time = new Timer();
        time.schedule(new EntityApparitionTask(entity, spawnField, spawnPos, ef), delay);
    }

    private static int getDelay() {
        return getDelay(2000, 10000);
    }

    public static int getDelay(final int min, final int max) {
        // TODO: get delay from TimingSystem (which should be broken out from MusicSystem)
        return min + rand.nextInt((max - min) + 1);
    }

    private static void initWarpInEffects(AssetManager assMan, RenderSystem renderSystem, Vector2 pos, int duration) {
        // initializes the warp-in particle effect(s) and sound(s) for a CA effect or entity
        // pos : position of the warp-in
        // duration : milliseconds until warp-in complete
        ParticleEffect particleEffect = assMan.getParticleEffect(ParticleEff.PREWARP);
        particleEffect.setPosition(pos.x, pos.y);
        // NOTE: the following assumes that the first emitter in the particle effect is the longest:
        particleEffect.setDuration(
                duration - (int) particleEffect.getEmitters().get(0).duration  // adjust for max particle lifespan
        );
//        particleEffect.allowCompletion();  // optional? (maybe only needed if continuous==true)
        particleEffect.start();
        renderSystem.registerOrphanParticleEffect(particleEffect);
        logger.debug("added new particle len= " + duration + " @ (" + pos.x + ',' + pos.y + ")");
    }
}
