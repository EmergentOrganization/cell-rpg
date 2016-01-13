package com.emergentorganization.cellrpg.components.SpontaneousGeneration;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Component to describe influence the area around the entity which causes
 * sporadic stamping of the surrounding ca grid with a set pattern.
 * <p/>
 * Future work could re-implement this component as a list of SpontGen objects,
 * but to keep things simple for now entities are limited to having only 1
 * spontaneous generation.
 */
public class SpontaneousGenerationList extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    public float radius = 1;  // area around entity which may be stamped
    public float frequency = -1;  // how often the stamp will occur
    public float variance = 0;  // how much the timing of the can vary randomly
    public int sinceLastGenerationCounter = 0;  // counter for determining when it's time to generate

    public ArrayList<CALayer> layers = new ArrayList<CALayer>();  // list of layers that might be stamped
    public ArrayList<int[][]> stampList = new ArrayList<int[][]>();  // list of stamps that may be applied

    public void clear() {
        // clears layers and stamps
        layers.clear();
        stampList.clear();
        frequency = -1;
        sinceLastGenerationCounter = 0;
    }

    public SpontaneousGeneration getRandomGeneration(Position entityPos, Bounds entityBounds) {
        // gets a random SpontaneousGeneration
        int layer = ThreadLocalRandom.current().nextInt(0, layers.size());
        int stamp = ThreadLocalRandom.current().nextInt(0, stampList.size());

        // TODO: exclude inner radius / bounds?
        Vector2 pos = entityPos.getCenter(entityBounds).add(
                (float) (2 * radius * Math.random() - radius),
                (float) (2 * radius * Math.random() - radius)
        );

        sinceLastGenerationCounter = 0;
        return new SpontaneousGeneration(layers.get(layer), stampList.get(stamp), pos);
    }

    public boolean readyForGen() {
        // returns true if it is time to insert stamp
//         logger.trace(sinceLastGenerationCounter + " not yet " + frequency);
        if (frequency < 1) {
            return false;
        } else {
            // TODO: incorporate variance
            return sinceLastGenerationCounter > frequency;
        }
    }
}
