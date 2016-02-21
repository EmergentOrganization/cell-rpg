package io.github.emergentorganization.cellrpg.systems;

import com.artemis.BaseSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * System
 */
public class TimingSystem extends BaseSystem {
    public static long LOOP_DURATION = 30 * 1000; // loops must be this length!

    private long _lastLoopTime;  // last time we looped around
    private Timer timer = new Timer();
    boolean scheduled = false;

    private final Logger logger = LogManager.getLogger(getClass());

    public TimingSystem() {
        _lastLoopTime = System.currentTimeMillis();
    }

    public long getTimeToNextMeasure() {
        // return the number of ms until the next measureddddd
        long res = getNextLoopTime() - System.currentTimeMillis();
        logger.debug("time to next measure: "  + res);
        if (res > 0){
            return res;
        } else {
            loop();
            return getTimeToNextMeasure();
        }
    }

    public long getTimeSinceLastMeasure() {
        // returns amount of ms elapsed since the last measure
        return System.currentTimeMillis() - getLastLoopTime();
    }

    public long getNextLoopTime(){
        // return unix time of next loop
        return getLastLoopTime() + LOOP_DURATION;
    }

    @Override
    public void processSystem(){
        if (!scheduled){
            timer.schedule(new ReLoop(), LOOP_DURATION);
            scheduled = true;
        }
    }

    private long getLastLoopTime(){
        // use this getter instead of the attribute so we can ensure that we never wait too long before looping
        long nextTime = _lastLoopTime + LOOP_DURATION;
        if (nextTime > 0){
            return _lastLoopTime;
        } else {
            loop();
            return _lastLoopTime;
        }
    }

    private void loop(){
        _lastLoopTime = System.currentTimeMillis();
        scheduled = false;
    }

    class ReLoop extends TimerTask {
        public void run() {
            loop();
        }
    }
}