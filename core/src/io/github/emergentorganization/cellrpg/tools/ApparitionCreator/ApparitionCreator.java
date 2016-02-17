package io.github.emergentorganization.cellrpg.tools.ApparitionCreator;

import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
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
    private final Logger logger = LogManager.getLogger(getClass());

    private static Random rand = new Random();


    public static void apparateCAEffect(SpontaneousGeneration spontGen, CAGridComponents targetGrid){
        Timer time = new Timer();
        int minDelay = 0;
        int maxDelay = 5000;
        // TODO: get delay from TimingSystem (which should be broken out from MusicSystem)
        long delay = minDelay + rand.nextInt((maxDelay - minDelay) + 1);
        time.schedule(new CAApparitionTask(spontGen, targetGrid), delay);

    }

    public static void apparateEntity(){
        // TODO
    }
}
