package io.github.emergentorganization.cellrpg.input.player.MovementControls;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * A utility class to record the screen coordinates with a delay between each "frame"
 */
public class CoordinateRecorder {

    public static float minPathLen = 100f;  // min length of path segment (in world units?)
    private final Logger logger = LogManager.getLogger(getClass());
    private float delay = 100; // Delay in ms
    private long lastRecord;
    private ArrayList<Vector2> coords = new ArrayList<Vector2>();

    public CoordinateRecorder(float delay) {
        this.delay = delay;
    }

    public void record(float x, float y) {
        if (TimeUtils.timeSinceMillis(lastRecord) >= delay) {
            Vector2 vect = new Vector2(x, y);
            if (farEnoughFromLastCoord(vect)) {
                lastRecord += delay;

                logger.trace("Recording " + x + ", " + y);
                coords.add(vect);
            }
        }
    }

    public boolean isEmpty() {
        return coords.isEmpty();
    }

    public Vector2 pop() {
        // gets first co-ord from queue, removes it from stack and returns it
        if (coords.isEmpty())
            return null;

        Vector2 v = coords.get(0);
        coords.remove(0);

        return v;
    }

    public ArrayList<Vector2> getCoords() {
        return coords;
    }

    public void clear() {
        coords.clear();
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    private boolean farEnoughFromLastCoord(Vector2 vect) {
        if (coords.size() < 1) {
            return true;
        } else {
            Vector2 last = coords.get(coords.size() - 1);
            return vect.cpy().sub(last).len2() > minPathLen;
        }
    }
}
