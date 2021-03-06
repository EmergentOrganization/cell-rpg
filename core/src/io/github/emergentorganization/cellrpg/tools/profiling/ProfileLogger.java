package io.github.emergentorganization.cellrpg.tools.profiling;

import com.badlogic.gdx.utils.PerformanceCounter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Class for logging profile data to a file for later analysis.
 */
class ProfileLogger {
    private static final Logger logger = LogManager.getLogger(ProfileLogger.class);

    public static void log(PerformanceCounter counter) {

        logger.trace("writing profiler log to file");

        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("profilerLog.csv", true), "utf-8")
            );
            writer.write(
                    counter.name + ','
                            + Float.toString(counter.time.average) + ','
                            + Float.toString(counter.time.min) + ','
                            + Float.toString(counter.time.max) + ','
                            + Float.toString(counter.load.average) + ','
                            + Float.toString(counter.load.min) + ','
                            + Float.toString(counter.load.max)
                            + "\n"
            );
        } catch (IOException ex) {
            logger.error("profileLog write exception: ", ex);
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (Exception ex) {/*ignore*/}
        }
    }
}
