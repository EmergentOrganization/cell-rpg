package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Having this component allows entities to spawn other entities nearby.
 * This is particularly useful for spawning collectible entities like power-ups.
 * <p/>
 * Modeled after SpontaneousGenerationList
 */
public class CollectibleSpawnField extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    public float radius = 1;  // area around entity which may be spawned
    public float frequency = -1;  // how often the spawn will occur
    public float variance = 0;  // how much the timing can vary randomly
    public int sinceLastSpawnCounter = 0;  // counter for determining when it's time to spawn

    public ArrayList<EntityID> entityList = new ArrayList<EntityID>();  // list entity classes that may be spawned

    public int getCollectible(Position entityPos, Bounds entityBounds, EntityFactory entFact) {
        // gets a random entity from the list, returns the id of entity created.
        // if entity not created returns -1
        sinceLastSpawnCounter = 0;
        if (entityList.size() > 0) {
            int ent_i = ThreadLocalRandom.current().nextInt(0, entityList.size());

            // TODO: exclude inner radius / bounds?
            Vector2 pos = entityPos.getCenter(entityBounds, 0).add(
                    (float) (2 * radius * Math.random() - radius),
                    (float) (2 * radius * Math.random() - radius)
            );

            float rotation = 0f;  // TODO: add rotation?

            logger.debug("entity spawned in spawnField");
            // instantiate one of the entities, return id
            return entFact.createEntityByID(entityList.get(ent_i), pos, rotation);
        } else {
            return -1;
        }
    }

    public boolean readyForSpawn() {
        // returns true if it is time to insert stamp
//         logger.trace(sinceLastGenerationCounter + " not yet " + frequency);
        if (frequency < 1) {
            return false;
        } else {
            // TODO: incorporate variance
            return sinceLastSpawnCounter > frequency;
        }
    }
}
