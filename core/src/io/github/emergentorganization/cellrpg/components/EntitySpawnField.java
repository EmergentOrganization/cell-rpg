package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.tools.TimingUtils;
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
public class EntitySpawnField extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    public float radius = 1;  // area around entity which may be spawned
    public float frequency = -1;  // how often the spawn will occur
    public float variance = 0;  // how much the timing can vary randomly
    public int sinceLastSpawnCounter = 0;  // counter for determining when it's time to spawn

    public ArrayList<EntityID> entityList = new ArrayList<EntityID>();  // list entity classes that may be spawned

    public int spawnEntity(Position entityPos, Bounds entityBounds, EntityFactory entFact) {
        // gets a random entity from the list, returns the id of entity created.
        // if entity not created returns -1
        sinceLastSpawnCounter = 0;
        if (entityList.size() > 0) {
            logger.trace("choosing from " + entityList.size() + " ents");
            return _spawnEntity(_getSpawnableEntity(), entityPos, entityBounds, entFact);
        } else {
            return -1;
        }
    }

    public EntityID _getSpawnableEntity(){
        // returns entity from entityList
        int ent_i = ThreadLocalRandom.current().nextInt(0, entityList.size());
        return entityList.get(ent_i);
    }

    public Vector2 getSpawnPosition(Position targetPos, Bounds targetBounds){
        return targetPos.getCenter(targetBounds, 0).add(
                (float) (2 * radius * Math.random() - radius),
                (float) (2 * radius * Math.random() - radius)
        );
    }

    public int _spawnEntityAt(EntityID entity, EntityFactory entFact, Vector2 pos){
        float rotation = 0f;  // TODO: add rotation?

        logger.debug("entity spawned in spawnField");
        // instantiate one of the entities, return id
        if (entity != null)
            return entFact.createEntityByID(entity, pos, rotation);
        else
            return -1;
    }

    public int _spawnEntity(EntityID entity, Position entityPos, Bounds entityBounds, EntityFactory entFact){
        // spawns entity of given Id
        // TODO: exclude inner radius / bounds?
        Vector2 pos = getSpawnPosition(entityPos, entityBounds);
        return _spawnEntityAt(entity, entFact, pos);
    }

    public boolean readyForSpawn() {
        // returns true if it is time to spawn entity.
        return !entityList.isEmpty() && TimingUtils.readyForPeriodicEvent(frequency, sinceLastSpawnCounter);
    }

    public void tick(){
        sinceLastSpawnCounter += 1;
    }
}
