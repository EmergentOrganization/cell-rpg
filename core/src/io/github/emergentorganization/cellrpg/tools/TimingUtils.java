package io.github.emergentorganization.cellrpg.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utilities to aid in calculating if a timer is up
 */
public class TimingUtils {
    private static final Logger logger = LogManager.getLogger(TimingUtils.class);

    public static boolean readyForPeriodicEvent(final float frequency, final float counter ){
        // returns true if counter is high enough to perform event of given frequency.
        if (frequency < 0) {  // never ready
            return false;
        } else if (frequency < 1) {  // maybe ready this round
            return counter > (1f / frequency);
        } else if( frequency >= 1) {  // ready (multiple times) every round
            return true;
        } else {
            logger.warn("unrecognized periodic event val:" + frequency);
            return false;
        }
    }
}
