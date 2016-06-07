package io.github.emergentorganization.cellrpg.tools.profiling;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.utils.ArtemisProfiler;
import com.badlogic.gdx.utils.PerformanceCounter;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Profiler to collect profiling information on artemis systems used within libgdx & the Emergent2dCore framework.
 *
 * Should be injected with:
 *
 *       @Profile(using=EmergentProfiler.class, enabled=GameSettings.devMode())
 */

public class EmergentProfiler implements ArtemisProfiler {
    private final Logger logger = LogManager.getLogger(getClass());
    private String className = "UNNAMED_CLASS";
    private final long LOG_PERIOD = 3*1000;  // profiler log frequency [ms]
    private long lastLog = System.currentTimeMillis();

    PerformanceCounter counter;

    public EmergentProfiler(){
        logger.trace("profiler constructed");
    }

    public void initialize(BaseSystem system, World world){
        String[] strs = system.toString().split("@")[0].split("\\.");
        className = strs[strs.length-1];  // last section split by '.' (but excluding after @) is class name
        logger.trace("init profiler on " + className);
        counter = new PerformanceCounter(className);
    }

    public void start(){
        if (GameSettings.devMode()) {
            counter.start();
        }
    }

    public void stop(){
        if (GameSettings.devMode()) {
            counter.stop();
            counter.tick();
            if (System.currentTimeMillis() - lastLog > LOG_PERIOD) {
                ProfileLogger.log(counter);
                lastLog = System.currentTimeMillis();
            } else {
                logger.trace(LOG_PERIOD - (System.currentTimeMillis() - lastLog) + "ms remain until log");
            }
        } else {
            logger.trace("devmode disabled");
        }
    }
}
