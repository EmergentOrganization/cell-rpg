package io.github.emergentorganization.cellrpg.core.systems.MusicSystem;

import com.artemis.BaseSystem;
import com.badlogic.gdx.audio.Sound;
import io.github.emergentorganization.cellrpg.core.SoundEffect;
import io.github.emergentorganization.cellrpg.core.systems.MoodSystem;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.systems.TimingSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;

/**
 * System for handling music based on game events.<br>
 * uses Gdx.audio.newSound() instead of newMusic() to avoid streaming overhead but sound files must be < 1MB<br>
 * <br>
 * NOTE: For multi-threaded tasks, please use {@link MusicSystem#runLater(Runnable)}<br>
 * <br>
 * ported from BgSoundController:<br>
 * https://github.com/EmergentOrganization/cell-rpg/blob/audioLayers/core/src/com/emergentorganization/cellrpg/sound/BgSoundController.java)
 */
public class MusicSystem extends BaseSystem {
    private final Logger logger = LogManager.getLogger(getClass());
    private final ArrayList<Runnable> tasks = new ArrayList<Runnable>();
    private final Timer loopTimer = new Timer();
    private MoodSystem moodSystem;
    private AssetManager assetManager;
    private TimingSystem timingSystem;
    private Sound[] constantLoops;  // loops which play constantly
    private ArrayList<Sound> unusedLoops = new ArrayList<Sound>();
    private ArrayList<Sound> currentLoops = new ArrayList<Sound>();  // currently playing loops
    private ArrayList<Sound> loopsToRemove = new ArrayList<Sound>(); // loops queued for removal next round
    private boolean prepped = false;  // flag used to track if next set of loops has been queued yet
    private boolean scheduled = false;

    public MusicSystem(AssetManager assetManager) {
        logger.trace("MusicSystem initializing...");
        this.assetManager = assetManager;
        start();
        logger.trace("MusicSystem initialized");
    }

    @Override
    public void processSystem() {
        // Run currently queued tasks
        synchronized (tasks) {
            for (Runnable task : tasks) {
                task.run();
            }
            tasks.clear();
        }

        long deltaTime = timingSystem.getTimeSinceLastMeasure();

        if (!scheduled && deltaTime > 25 * 1000) {
            // almost time to loop back around, schedule the reloop
            scheduleNextLoop();
        } else if (!prepped && deltaTime > 15 * 1000) {
            // halfway through the loop, prep next loop(s)
            prepNextLoopRound();
        }
    }

    public void runLater(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    /**
     * Stops the current song
     */
    public void stop() {
        for (Sound currentLoop : currentLoops) {
            currentLoop.stop();
            currentLoops.remove(currentLoop);
        }
    }

    /**
     * Starts a new song
     */
    public void start() {
        constantLoops = new Sound[2];
        constantLoops[0] = assetManager.getSoundEffect(SoundEffect.MUSIC_LOOP_PAD);
        constantLoops[1] = assetManager.getSoundEffect(SoundEffect.MUSIC_LOOP_KEYS);
        constantLoops[0].setLooping(constantLoops[0].play(), false);
        constantLoops[1].setLooping(constantLoops[1].play(), false);
    }

    private void scheduleNextLoop() {
        // schedules a new loop play
        logger.debug("scheduling next music loop");
        loopTimer.schedule(new LoopTask(this), timingSystem.getTimeToNextMeasure());
        logger.debug("schedule in " + timingSystem.getTimeToNextMeasure());
        scheduled = true;
    }

    private Sound getRandomSound() {
        int index = (int) Math.max(0, Math.floor((Math.random() * unusedLoops.size()) - 1));
        return unusedLoops.get(index);
    }

    private void prepNextLoopRound() {
        // preps next round of loops
        logger.debug("prepping next round of loops");
        // number of loops desired:
        int numberOfLoops = moodSystem.scoreIntensityLevelOutOf((short) (currentLoops.size() + unusedLoops.size()));
        numberOfLoops += 1; // we want 1 optional loop at minimum
        logger.debug("current # loops:" + currentLoops.size());
        logger.debug("desired # loops:" + numberOfLoops);

        // swap out a loops to add random variation
        if (currentLoops.size() > 0 && unusedLoops.size() > 0) {
            int randomCurrentLoopIndex = (int) Math.max(0, Math.floor((Math.random() * currentLoops.size()) - 1));
            loopsToRemove.add(currentLoops.get(randomCurrentLoopIndex));
            int randomUnusedLoopIndex = (int) Math.max(0, Math.floor((Math.random() * unusedLoops.size()) - 1));
            currentLoops.add(unusedLoops.get(randomUnusedLoopIndex));
        }

        while (numberOfLoops < currentLoops.size() - loopsToRemove.size()
                && loopsToRemove.size() != currentLoops.size()) {
            // queue loops for removal
            loopsToRemove.add(currentLoops.get(loopsToRemove.size()));
        }

        while (numberOfLoops > currentLoops.size() && unusedLoops.size() > 0) {
            // add loops
            Sound newSound = getRandomSound();
            currentLoops.add(newSound);
            unusedLoops.remove(newSound);
        }

        prepped = true;
    }

    /**
     * Disposes at the end of the CellRPG lifecycle. Do not dispose on scene change -- see {@link MusicSystem#stop}
     */
    public void dispose() {
        logger.info("Disposing Music System!");
        // set these to try to help other threads stop:
        loopTimer.cancel();
        loopTimer.purge();
        scheduled = true;
        prepped = false;

        // Stop all music loops in preparation for scene change. Do not dispose of loops; it is managed by GdxAssetManager
        for (Sound currentLoop : currentLoops) {
            currentLoop.stop();
        }
        for (Sound unusedLoop : unusedLoops) {
            unusedLoop.stop();
        }
        for (Sound constantLoop : constantLoops) {
            if (constantLoop != null)
                constantLoop.stop();
        }

        for (Sound loop : loopsToRemove) {
            loop.stop();
        }
    }

    /**
     * Cycles the current loops to add variation to the tune
     */
    void updateCurrentLoops() {
        // executes the planned changes to currentLoops
        // remove loops queued for removal
        for (Sound loop : loopsToRemove) {
            currentLoops.remove(loop);
            unusedLoops.add(loop);
        }
        // loops are not queued for addition, they are added (but not played)immediately
    }

    boolean isPrepped() {
        return prepped;
    }

    void playConstantLoops() {
        for (Sound constantLoop : constantLoops) {
            constantLoop.play();
        }
    }

    void playCurrentLoops() {
        for (Sound loop : currentLoops) {
            if (loop != null) {
                loop.play();
            }
        }
    }

    void onLoopCompleted() {
        prepped = false;
        scheduled = false;
    }
}
