package com.emergentorganization.cellrpg.systems.CASystems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.emergentorganization.cellrpg.components.Bounds;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
import com.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This system handles semi-random insertion of CA patterns into an
 *  entity's surrounding area (spontaneous generation) as defined
 *  by the entity's SpontaneousGenerationList component.
 *
 * Created by 7yl4r on 1/1/2016.
 */
public class CASpontaneousGenerationSystem extends IntervalIteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    private ComponentMapper<Position> pos_m;
    private ComponentMapper<Bounds> bounds_m;
    private ComponentMapper<SpontaneousGenerationList> spontGen_m;

    public CASpontaneousGenerationSystem(){
        super(Aspect.all(SpontaneousGenerationList.class, Position.class, Bounds.class), 1);
    }

    public void process(int id){
        TagManager tagMan = world.getSystem(TagManager.class);
        SpontaneousGenerationList genList = spontGen_m.get(id);
        if (genList.readyForGen()){
            spontaneousGenerate(id, genList, tagMan);
        } else {
            genList.sinceLastGenerationCounter += 1;
        }
    }

    private void spontaneousGenerate(int id, SpontaneousGenerationList genList, TagManager tagMan){
        // performs the given spontaneous generation.
        Position pos = pos_m.get(id);
        Bounds bound = bounds_m.get(id);
        SpontaneousGeneration spontGen = genList.getRandomGeneration(pos, bound);

        Entity CAEnt = tagMan.getEntity(spontGen.targetLayer.toString());
        CAGridComponents targetGrid = CAEnt.getComponent(CAGridComponents.class);

        logger.trace("spontaneous Generate @ " + pos);
        targetGrid.stampCenteredAt(spontGen.stamp, spontGen.position);
    }
}
