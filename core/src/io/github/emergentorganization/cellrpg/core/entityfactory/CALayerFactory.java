package io.github.emergentorganization.cellrpg.core.entityfactory;

import com.artemis.Archetype;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.components.CellType;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.core.entityfactory.builder.EntityBuilder;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.CellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CA;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.emergent2dcore.systems.CameraSystem;
import io.github.emergentorganization.emergent2dcore.systems.MoodSystem;

/**
 * !!! DISCLAIMER: despite the name, this does not follow the factory pattern. !!! TODO: rename
 * <p/>
 * Methods for building different types of CA layer entities by initializing the CAGridComponent.
 * Adapted from LayerBuilder (10-10-2015) on 12-12-2015 by 7yl4r.
 * <p/>
 * TODO: this should be part of the CALayerComponentsConstructor, not a separate class.
 */
public class CALayerFactory {

    public static Entity buildLayer(World world, Vector2 pos, Archetype layer_arch,
                                  String descr, String tag, CALayer layer){
        Camera camera = world.getSystem(CameraSystem.class).getGameCamera();
        Entity vyroidLayer = new EntityBuilder(world, layer_arch, descr,
                layer.toString(), pos)
                .tag(tag)
                .build();
        CAGridComponents vyroidLayerStuff = vyroidLayer.getComponent(CAGridComponents.class);
        CALayerFactory.initLayerComponentsByType(vyroidLayerStuff, layer, camera);
        try {
            vyroidLayerStuff.intensityPerCell = MoodSystem.CA_INTENSITY_MAP.get(tag);
        } catch (NullPointerException ex){
            ; // this layer has no effect on intensity (no problem)
        }

        return vyroidLayer;
    }

    public static void initLayerComponentsByType(CAGridComponents layerComponents, CALayer layerType, Camera camera) {
        // initializes given components such that they conform to the given type
        layerCompSetup(layerComponents, layerType);
        layerComponents.init(camera);
    }

    private static void layerCompSetup(CAGridComponents layerComponents, CALayer layerType) {
        // start out with the default settings
        layerComponents.stateColorMap = new Color[]{new Color(1f, .2f, .2f, 1f), new Color(1f, .4f, .8f, .8f)};
        layerComponents.cellSize = 3;
        layerComponents.cellType = CellType.WITH_HISTORY;
        layerComponents.renderType = CellRenderer.COLOR_MAP;
        layerComponents.ca = CA.BUFFERED;
        layerComponents.TIME_BTWN_GENERATIONS = 100;

        // and then overwrite defaults as required based on type
        switch (layerType) {
            case VYROIDS_MINI:
                layerComponents.cellSize = 1;
                layerComponents.stateColorMap = new Color[]{new Color(1f, .87f, .42f, 1f), new Color(1f, .4f, .8f, .8f)};
                return;

            case VYROIDS_MEGA:
                layerComponents.cellSize = 11;
                layerComponents.stateColorMap = new Color[]{new Color(.2f, .2f, 1f, 1f), new Color(1f, .4f, .8f, .8f)};
                return;

            case VYROIDS:
                return;

            case VYROIDS_GENETIC:
                layerComponents.cellSize = 11;//35
                layerComponents.renderType = CellRenderer.GENETIC;
                layerComponents.cellType = CellType.GENETIC;
                layerComponents.ca = CA.GENETIC;
                return;

            case ENERGY:
                layerComponents.cellSize = 3;
                layerComponents.stateColorMap = DecayCellRenderer.stateColorMap;
                layerComponents.renderType = CellRenderer.DECAY;
                layerComponents.cellType = CellType.DECAY;
                layerComponents.ca = CA.DECAY;
                return;
        }
    }
}
