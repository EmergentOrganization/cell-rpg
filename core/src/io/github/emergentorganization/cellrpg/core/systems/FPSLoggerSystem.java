package io.github.emergentorganization.cellrpg.core.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * logs FPS to file for later analyss. Based on libGDX's FPSLogger.
 */
public class FPSLoggerSystem extends BaseSystem {
    private static final Logger logger = LogManager.getLogger(FPSLoggerSystem.class);

    private final String LOGFILE = "FPSLog.csv";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat ("hh:mm:ss.SSS");

    long startTime;

    public FPSLoggerSystem(){
        startTime = TimeUtils.nanoTime();
    }

    @Override
    public void processSystem(){
        if (GameSettings.devMode()){
            if (TimeUtils.nanoTime() - startTime > 1000000000) /* 1,000,000,000ns == one second */{
                logFPS();
                startTime = TimeUtils.nanoTime();
            }
        }
    }

    private void logFPS(){
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(LOGFILE, true), "utf-8")
            );
            writer.write(
                    dateFormat.format(new Date()) + ',' + Gdx.graphics.getFramesPerSecond() + '\n'
            );
        } catch (IOException ex) {
            logger.error("FPSLog write exception: ", ex);
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }
}
