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

/**
 * Having this component allows entities to spawn other entities nearby.
 * This is particularly useful for spawning collectible entities like power-ups.
 * <p/>
 * Modeled after SpontaneousGenerationList
 */
public class EntitySpawnField extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    public float radius = 1;  // area around entity which may be spawned
    public long period = -1;  // how often the spawn will occur [ms]
    private long lastSpawn = 0;  // counter for determining when it's time to spawn

    public final ArrayList<EntityID> entityList = new ArrayList<EntityID>();  // list entity classes that may be spawned

    public Vector2 getSpawnPosition(Position targetPos, Bounds targetBounds) {
        return targetPos.getCenter(targetBounds, 0).add(
                (float) (2 * radius * Math.random() - radius),
                (float) (2 * radius * Math.random() - radius)
        );
    }

    public int spawnEntity(EntityID entity, EntityFactory entFact, Vector2 pos) {
        float rotation = 0f;  // TODO: add rotation?

        logger.debug("entity spawned in spawnField");
        // instantiate one of the entities, return id
        if (entity != null)
            return entFact.createEntityByID(entity, pos, rotation);
        else
            return -1;
    }

    /**
     * Get spawnable entity from the SpawnField
     * @return An entity if ready to spawn <br>
     *         Null if not ready to spawn from this field
     */
    public EntityID getSpawnableEntity() {
        // returns true if it is time to spawn entity.
        boolean ready = !entityList.isEmpty()
                && period > 0
                && System.currentTimeMillis()-lastSpawn > period
        ;

        if (ready) {
            lastSpawn = System.currentTimeMillis(); // safe to assume this constitutes a spawn event?
            int ent_i = (int) (Math.random() * entityList.size());
            return entityList.get(ent_i);
        }
        else {
            return null;
        }
    }
}
