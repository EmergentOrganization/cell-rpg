package com.emergentorganization.cellrpg.sound;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.emergentorganization.cellrpg.tools.FileStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by BrianErikson on 10/17/15.
 */
public class BgSoundController {
    public static float LOOP_DURATION = 30.0f; // loops must be this length!
    public static float FADE_TIME = 2f; // in seconds
    public static float START_FADE = LOOP_DURATION - FADE_TIME;
    public static float FADE_OUT_CUTOFF = 0.01f;
    private Logger logger;
    private FileHandle[] loops;
    private FileHandle lastLoopHandle;
    private Music currentLoop;
    private Fade fade;

    private BgSoundController() {}

    private static class BgSoundControllerHolder {
        private static final BgSoundController INSTANCE = new BgSoundController();
    }

    public static BgSoundController fetch() {
        return BgSoundControllerHolder.INSTANCE;
    }

    public void initialize() {
        logger = LogManager.getLogger(getClass());
        FileHandle dir = Gdx.files.getFileHandle(FileStructure.RESOURCE_DIR + "sounds" + File.separator + "arcade_30s_loops", Files.FileType.Internal);
        loops = dir.list();
    }

    public void start() {
        next();
    }

    /**
     * Stops the current song and future iterations
     */
    public void stop() {
        currentLoop.stop();
        currentLoop.dispose();
        currentLoop = null;
        fade = null;
    }

    public void step(float deltaTime) {
        if (currentLoop != null) {
            //System.out.println(currentLoop.getPosition() / START_FADE );
            if (currentLoop.getPosition() / START_FADE >= 1.0f && fade == null) {
                fadeOut(FADE_TIME);
            }

            if (fade != null && fade.duration > 0) {
                fade.timeLeft -= deltaTime;
                if (fade.in) {
                    float volume = Math.min(1.0f, 1 - (fade.timeLeft / fade.duration));
                    currentLoop.setVolume(volume);
                    if (volume >= 1.0f)
                        fade = null;
                }
                else {
                    float volume = Math.max(0, fade.timeLeft / fade.duration);
                    currentLoop.setVolume(volume);

                    if (volume <= FADE_OUT_CUTOFF) {
                        next();
                    }
                }
            }
        }
    }

    public void next() {
        if (currentLoop != null) {
            currentLoop.stop();
            currentLoop.dispose();
        }
        currentLoop = getRandomMusic();
        currentLoop.play();
        fadeIn(FADE_TIME);
    }

    private void fadeIn(float duration) {
        currentLoop.setVolume(0.0f);
        fade = new Fade(true, duration);
    }

    private void fadeOut(float duration) {
        currentLoop.setVolume(1.0f);
        fade = new Fade(false, duration);
    }

    private Music getRandomMusic() {
        int index = (int) Math.max(0, Math.floor((Math.random() * loops.length) - 1));

        // Don't play the same song twice in a row
        if (lastLoopHandle == loops[index])
            index = (int) Math.max(0, Math.floor((Math.random() * loops.length) - 1));
        else
            lastLoopHandle = loops[index];

        logger.info("Now playing: " + lastLoopHandle.name());
        return Gdx.audio.newMusic(loops[index]);
    }

    /**
     * Disposes at the end of the CellRPG lifecycle. Do not dispose on scene change -- see {@link BgSoundController#stop}
     */
    public void dispose() {
        if (currentLoop != null)
            currentLoop.dispose();
    }

    public class Fade {
        public boolean in;
        public float duration;
        public float timeLeft;

        public Fade(boolean in, float duration) {
            this.in = in;
            this.duration = duration;
            timeLeft = duration;
        }
    }
}
