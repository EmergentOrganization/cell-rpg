package io.github.emergentorganization.cellrpg.scenes.game.regions;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TheEdge implements iRegion {

    public TheEdge(Scene parentScene) {
        super();
    }

    public CALayer[] getCALayers() {
        return new CALayer[]{
                CALayer.ENERGY,
                CALayer.VYROIDS
        };
    }

    public void loadRegion(World world) {

    }

    public void enterRegion(World world) {
        logger.info("entering the edge region");
        setCAEdgeSpawns(world);
    }

    private void setCAEdgeSpawns(World world) {
        TagManager tagMan = world.getSystem(TagManager.class);

        Entity CAEnt = tagMan.getEntity(CALayer.VYROIDS.getTag());
        CAGridComponents CAComp = CAEnt.getComponent(CAGridComponents.class);
        CAComp.edgeSpawner = CAEdgeSpawnType.RANDOM_SPARSE;

        tagMan.getEntity(CALayer.VYROIDS_GENETIC.getTag()).getComponent(CAGridComponents.class).edgeSpawner
                = CAEdgeSpawnType.RANDOM_50_50;
    }

    public iRegion getNextRegion(World world) {
        return null;  // TODO: return adjoining region when nearing boundary
    }

    private final Logger logger = LogManager.getLogger(getClass());
}
