package io.github.emergentorganization.cellrpg.tools.ApparitionCreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.emergent2dcore.systems.RenderSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Timer;

/**
 * Static methods for warping in entities to appear in the game.
 * Why?
 *  This should help add pre-warp particle effects and sounds.
 *  It may also be useful to allow us to time warp-ins to sync
 *  up with musical queues.
 *
 *  PS: it's called ApparitionCreator because it helps things appear...
 *              ...Someone should proabably come up with a better name.
 */
public class ApparitionCreator {
    private static final Logger logger = LogManager.getLogger(ApparitionCreator.class);

    private static Random rand = new Random();


    public static void apparateCAEffect(RenderSystem renderSys, SpontaneousGeneration spontGen, CAGridComponents targetGrid){
        Timer time = new Timer();
        int minDelay = 2000;
        int maxDelay = 10000;
        // TODO: get delay from TimingSystem (which should be broken out from MusicSystem)
        int delay = minDelay + rand.nextInt((maxDelay - minDelay) + 1);
        initWarpInEffects(renderSys, spontGen.position, delay);
        time.schedule(new CAApparitionTask(spontGen, targetGrid), delay);
    }

    public static void apparateEntity(){
        // TODO
//        initWarpInEffects();
    }

    private static void initWarpInEffects(RenderSystem renderSystem, Vector2 pos, int duration){
        // initializes the warp-in particle effect(s) and sound(s) for a CA effect or entity
        // pos : position of the warp-in
        // duration : milliseconds until warp-in complete
        ParticleEffect particleEffect = new ParticleEffect();
        // TODO: use pooling for loading: https://www.youtube.com/watch?v=3OwIiELYa70
        // TODO: use AssetManager here
        particleEffect.load(Gdx.files.internal("resources/particleEffects/prewarp.p"), Gdx.files.internal("resources/particleEffects"));
        particleEffect.setPosition(pos.x, pos.y);
        particleEffect.scaleEffect(EntityFactory.SCALE_WORLD_TO_BOX/2f);
        particleEffect.setDuration(duration);
        particleEffect.start();
        renderSystem.registerOrphanParticleEffect(particleEffect);
        logger.debug("added new particle len= " + duration + " @ (" + pos.x + ',' + pos.y + ")");
    }
}
