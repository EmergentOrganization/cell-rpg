package io.github.emergentorganization.cellrpg.tools.ApparitionCreator;

import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

/**
 * defines the appearance of a given entity.
 */
public class EntityApparitionTask extends TimerTask {
    private final Logger logger = LogManager.getLogger(getClass());

    Vector2 spawnLoc;
    EntityFactory ef;
    EntityID entityToSpawn;
    EntitySpawnField spawnField;

    public EntityApparitionTask(EntityID entityToSpawn, EntitySpawnField spawnField, Vector2 spawnLocation, EntityFactory ef) {
        spawnLoc = spawnLocation;
        this.spawnField = spawnField;
        this.entityToSpawn = entityToSpawn;
        this.ef = ef;
    }

    public EntityApparitionTask(EntityID entityToSpawn, EntitySpawnField spawnField, Position pos, Bounds bound, EntityFactory ef) {
        this(entityToSpawn, spawnField, spawnField.getSpawnPosition(pos, bound), ef);
    }

    public void run() {
        logger.debug("spawn ent " + entityToSpawn + " @" + spawnLoc + ") using E.F.=" + ef);
        spawnField._spawnEntityAt(entityToSpawn, ef, spawnLoc);
    }
}
