package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Region which is expected to expire in a set amount of time, and can be made to expire more quickly.
 */
public abstract class TimedRegion implements iRegion{
    public static final long NEVER_EXPIRE = -1;  // set maxLength to this for infinite region lifespan

    public long maxLength;  // max time before switching to next region
    private long enterTime;  // time region is entered

    private final Logger logger = LogManager.getLogger(getClass());

    public TimedRegion(final long expiresIn){
        maxLength = expiresIn;
    }

    public void enterRegion(World world) {
        enterTime = System.currentTimeMillis();
    }

    public iRegion getNextRegion(World world) {
        if (timeExpired()) {
            logger.info("leaving SingleShapeWarpRegion");
            return _getNextRegion();
        } else {
            return null;
        }
    }

    protected iRegion _getNextRegion(){  // NOTE: perhaps this should be abstract & logic should be in child classes
        return RegionBuildTool.getNextRegion(this);
    }

    private boolean timeExpired(){
        // return true if time is up
        long length = maxLength;
        // NOTE: could shorten length here based on player score or other criterion

        long timeSpent = System.currentTimeMillis() - enterTime;
//        logger.trace("time left in region: " + (length-timeSpent));
        return timeSpent > length;
    }
}
