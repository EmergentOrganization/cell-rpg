package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Region which is expected to expire in a set amount of time, and can be made to expire more quickly.
 */
public abstract class TimedRegion implements iRegion {
    public static final long NEVER_EXPIRE = -1;  // set maxLength to this for infinite region lifespan
    private final Logger logger = LogManager.getLogger(getClass());
    public final long maxLength;  // max time before switching to next region
    private final long minLength = 7 * 1000;  // min time before switching to next region
    private long enterTime;  // time region is entered

    TimedRegion(final long expiresIn) {
        maxLength = expiresIn;
    }

    public void enterRegion(World world) {
        enterTime = System.currentTimeMillis();
    }

    boolean readyForNextRegion(World world) {
        // return true if we're ready to move to the next region
        return timeExpired();
    }

    public iRegion getNextRegion(World world) {
        // return new region if it's time to leave this one, else return null
        if (_readyForNextRegion(world)) {
            logger.info("leaving TimedRegion. Time spent in region:" + getTimeInRegion());
            return _getNextRegion();
        } else {
            return null;
        }
    }

    private boolean _readyForNextRegion(World world) {
        // checks readyForNextRegion and checks result to ensure we aren't trying to move regions too quickly
        return readyForNextRegion(world) && getTimeInRegion() > minLength;
    }

    iRegion _getNextRegion() {  // NOTE: perhaps this should be abstract & logic should be in child classes
        // actually get & return the next region
        return RegionBuildTool.getNextRegion(this);
    }

    private long getTimeInRegion() {
        // returns amount of time which has been spent in region
        return System.currentTimeMillis() - enterTime;
    }

    private boolean timeExpired() {
        // return true if time is up
        long length = maxLength;
        // NOTE: could shorten length here based on player score or other criterion
        return getTimeInRegion() > length;
    }
}
