package io.github.emergentorganization.cellrpg.systems.CASystems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalIteratingSystem;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.tools.ApparitionCreator.ApparitionCreator;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.emergent2dcore.components.Position;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGeneration;
import io.github.emergentorganization.cellrpg.components.SpontaneousGeneration.SpontaneousGenerationList;
import io.github.emergentorganization.emergent2dcore.systems.RenderSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This system handles semi-random insertion of CA patterns into an
 * entity's surrounding area (spontaneous generation) as defined
 * by the entity's SpontaneousGenerationList component.
 */
public class CASpontaneousGenerationSystem extends IntervalIteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());

    private ComponentMapper<Position> pos_m;
    private ComponentMapper<Bounds> bounds_m;
    private ComponentMapper<SpontaneousGenerationList> spontGen_m;
    private RenderSystem renderSystem;
    private AssetManager assetManager;

    public CASpontaneousGenerationSystem() {
        super(Aspect.all(SpontaneousGenerationList.class, Position.class, Bounds.class), 1);
    }

    public void process(int id) {
        TagManager tagMan = world.getSystem(TagManager.class);
        SpontaneousGenerationList genList = spontGen_m.get(id);
        if (genList.readyForGen()) {
            spontaneousGenerate(id, genList, tagMan);
        } else {
            genList.tick();
        }
    }

    private void spontaneousGenerate(int id, SpontaneousGenerationList genList, TagManager tagMan) {
        // performs the given spontaneous generation.
        Position pos = pos_m.get(id);
        Bounds bound = bounds_m.get(id);
        for (int i = 0; i < genList.getAmountToGenerate(); i++) {
            SpontaneousGeneration spontGen = genList.getRandomGeneration(pos, bound);
            if (spontGen != null) {
                Entity CAEnt = tagMan.getEntity(spontGen.targetLayer.toString());
                CAGridComponents targetGrid = CAEnt.getComponent(CAGridComponents.class);

                logger.trace("init spontaneous Generate @ " + pos);
                ApparitionCreator.apparateCAEffect(assetManager, renderSystem, spontGen, targetGrid);
            }
        }
    }
}
