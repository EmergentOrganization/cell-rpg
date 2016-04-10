package io.github.emergentorganization.cellrpg.tools.ApparitionCreator;

import io.github.emergentorganization.cellrpg.components.EntitySpawnField;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

/**
 * defines the appearance of a given entity.
 */
public class EntityApparitionTask extends TimerTask {
    private final Logger logger = LogManager.getLogger(getClass());

    Position pos;
    Bounds bound;
    EntityFactory ef;
    EntityID entityToSpawn;
    EntitySpawnField spawnField;

    public EntityApparitionTask(EntityID entityToSpawn, EntitySpawnField spawnField, Position pos, Bounds bound, EntityFactory ef){
        this.spawnField = spawnField;
        this.entityToSpawn = entityToSpawn;
        this.pos = pos;
        this.bound = bound;
        this.ef = ef;
    }

    public void run(){
        logger.debug("spawn ent " + entityToSpawn + " @"+pos + "(s="+bound+") using E.F.=" + ef);
        spawnField._spawnEntity(entityToSpawn, pos, bound, ef);
    }
}
