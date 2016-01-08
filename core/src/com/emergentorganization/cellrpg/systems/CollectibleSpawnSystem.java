package com.emergentorganization.cellrpg.systems;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.CollectibleSpawnField;
import com.emergentorganization.cellrpg.components.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by 7yl4r on 1/2/2016.
 */
public class CollectibleSpawnSystem extends IntervalIteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    @Wire
    private com.emergentorganization.cellrpg.core.entityfactory.EntityFactory ef;

    private ComponentMapper<Position> pos_m;
    private ComponentMapper<Bounds> bounds_m;
    private ComponentMapper<CollectibleSpawnField> spawnField_m;

    public CollectibleSpawnSystem(){
        super(Aspect.all(Position.class, Bounds.class, CollectibleSpawnField.class), 1);
    }

    public void process(int id){
        TagManager tagMan = world.getSystem(TagManager.class);
        CollectibleSpawnField spawnField = spawnField_m.get(id);
        if (spawnField.readyForSpawn()){
            spawn(id, spawnField, tagMan);
        } else {
            spawnField.sinceLastSpawnCounter += 1;
        }
    }

    private void spawn(int id, CollectibleSpawnField spawnField, TagManager tagMan){
        // spawns entity in the field.
        Position pos = pos_m.get(id);
        Bounds bound = bounds_m.get(id);
        int newEntityId = spawnField.getCollectible(pos, bound, ef);
    }
}
