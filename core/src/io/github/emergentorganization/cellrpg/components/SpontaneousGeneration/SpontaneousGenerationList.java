package io.github.emergentorganization.cellrpg.components.SpontaneousGeneration;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.tools.TimingUtils;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
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
    public float frequency = -1;  // how many stamps per generation round. (larger = more stamps)
    // frequency can be:
    //      < 0 for never,
    //      0 < f < 1 for intervals,
    //      1 for 1/gen,
    //      integer value > 1 for multiple spontGens per cycle (non-integers will be rounded)
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

    public SpontaneousGeneration getRandomGeneration(Position entityPos, Bounds entityBounds) throws IllegalStateException {
        // gets a random SpontaneousGeneration
        logger.debug("choosing spontGen from " + layers.size() + " layers & " + stampList.size() + " stamps");

        try {
            int layer = ThreadLocalRandom.current().nextInt(0, layers.size());
            int stamp = ThreadLocalRandom.current().nextInt(0, stampList.size());
            // TODO: exclude inner radius / bounds?
            Vector2 pos = entityPos.getCenter(entityBounds, 0).add(
                    (float) (2 * radius * Math.random() - radius),
                    (float) (2 * radius * Math.random() - radius)
            );

            sinceLastGenerationCounter = 0;
            return new SpontaneousGeneration(layers.get(layer), stampList.get(stamp), pos);
        } catch( IllegalArgumentException ex){
            logger.warn("can't getSpontGen: layer or stamp list empty!");
            return null;
        }
    }

    public boolean readyForGen() {
        // returns true if it is time to insert stamp
        return TimingUtils.readyForPeriodicEvent(frequency, sinceLastGenerationCounter);
    }

    public void tick(){
        sinceLastGenerationCounter += 1;
    }

    public int getAmountToGenerate(){
        // returns number of spontGens that should be produced to satisfy generation rate set by frequency
        if (frequency > 1) {
            return Math.round(frequency);
        } else if (frequency > 0){
            return 1;
        } else {
            logger.warn("request to generate on SpontGenList.freq < 0");
            return 0;
        }
    }

    public void addLayer(CALayer newLayer){
        // adds a layer to layerList only if it is not already in the list
        if (layers.contains(newLayer)){
            return;
        } else {
            layers.add(newLayer);
        }
    }
}
