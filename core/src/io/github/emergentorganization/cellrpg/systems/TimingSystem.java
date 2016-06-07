package io.github.emergentorganization.cellrpg.systems;

import com.artemis.BaseSystem;
import io.github.emergentorganization.cellrpg.core.systems.MusicSystem.MusicSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimingSystem<br>
 * <br>
 * NOTE: For multi-threaded tasks, please use {@link MusicSystem#runLater(Runnable)}<br>
 * <br>
 */
public class TimingSystem extends BaseSystem {
    public static long LOOP_DURATION = 30 * 1000; // loops must be this length!
    private final Logger logger = LogManager.getLogger(getClass());
    private final ArrayList<Runnable> tasks = new ArrayList<Runnable>();
    boolean scheduled = false;
    private long _lastLoopTime;  // last time we looped around
    private Timer timer = new Timer();

    public TimingSystem() {
        _lastLoopTime = System.currentTimeMillis();
    }

    public long getTimeToNextMeasure() {
        // return the number of ms until the next measure
        long res = getNextLoopTime() - System.currentTimeMillis();
        logger.trace("time to next measure: " + res);
        if (res > 0) {
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

    public long getNextLoopTime() {
        // return unix time of next loop
        return getLastLoopTime() + LOOP_DURATION;
    }

    public void runLater(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
        }
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

        if (!scheduled) {
            timer.schedule(new ReLoop(), LOOP_DURATION);
            scheduled = true;
        }
    }

    private long getLastLoopTime() {
        // use this getter instead of the attribute so we can ensure that we never wait too long before looping
        long nextTime = _lastLoopTime + LOOP_DURATION;
        if (nextTime > 0) {
            return _lastLoopTime;
        } else {
            loop();
            return _lastLoopTime;
        }
    }

    private void loop() {
        _lastLoopTime = System.currentTimeMillis();
        scheduled = false;
    }

    class ReLoop extends TimerTask {
        public void run() {
            runLater(new Runnable() {
                @Override
                public void run() {
                    loop();
                }
            });
        }
    }
}