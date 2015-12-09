package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CAGrid.CAGridBase;
import com.emergentorganization.cellrpg.systems.CARenderSystem.layers.CALayer;
import com.emergentorganization.cellrpg.systems.CARenderSystem.layers.LayerBuilder;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Renders cellular automata layers globally by selecting the entity with the CameraFollow component.
 *
 * Created by 7yl4r on 2015-11-18.
 */
@Wire
public class CARenderSystem extends BaseEntitySystem {

    // TODO: store caLayer state in manager(?), only do rendering here.
    protected Map<CALayer, CAGridBase> ca_layers = new EnumMap<CALayer, CAGridBase>(CALayer.class);

    private final LinkedList<Integer> sortedEntityIds;

    private final ShapeRenderer renderer;
    private final Logger logger = LogManager.getLogger(getClass());

    // variables injected (by Artemis.World) @ runtime:
    private ComponentMapper<Position> pm;
    private CameraSystem cameraSystem;
    private AssetManager assetManager;

    public CARenderSystem(ShapeRenderer shapeRenderer) {
        super(Aspect.all(CameraFollow.class));  // select only (assumed 1) camera-followed component (a bit hacky)

        this.renderer = shapeRenderer;
        sortedEntityIds = new LinkedList<Integer>();
    }

    private void updateLayerList(Camera camera){
        // updates layer list such that it contains only the ca layers it needs
        //    (so we don't waste time computing layers we're not using)
        // TODO: add / remove layers based on their usage. How? Maybe by checking # "live" cells in layer?
        if (ca_layers.values().size() < 1) {
            LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS).added(camera);
            LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS_GENETIC).added(camera);
            LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS_MEGA).added(camera);
            LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS_MINI).added(camera);
            LayerBuilder.addVyroidLayer(ca_layers, CALayer.ENERGY).added(camera);
        }
    }

    @Override
    protected  void begin() {
        Camera camera = cameraSystem.getGameCamera();

        updateLayerList(camera);

        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(cameraSystem.getGameCamera().combined);  // this should be uncommented, but doing so breaks cagrid...
        renderer.begin();
    }

    @Override
    protected  void processSystem() {
        Camera camera = cameraSystem.getGameCamera();

        for ( Map.Entry<CALayer, CAGridBase> entry : ca_layers.entrySet()) {
            CALayer layerKey = entry.getKey();
            CAGridBase layer = entry.getValue();

            layer.reposition(cameraSystem.getGameCamera());
            layer.renderGrid(renderer, camera);
        }
    }

    @Override
    protected void end() {
        renderer.end();
    }

    @Override
    protected  void inserted(int entityId) {

        sortedEntityIds.add(entityId);
//        Collections.sort(sortedEntityIds, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                Visual v1 = vm.get(o1);
//                Visual v2 = vm.get(o2);
//                return v1.index.ordinal() - v2.index.ordinal();
//            }
//        });
    }

    @Override
    protected void removed(int entityId) {
        ListIterator<Integer> iter = sortedEntityIds.listIterator();

        while (iter.hasNext()) {
            Integer id = iter.next();
            if (id - entityId == 0) {
                iter.remove();
            }
        }
    }

    public List<Integer> getSortedEntityIds() {
        return Collections.unmodifiableList(sortedEntityIds);
    }

    protected BaseCell newCell(int init_state){
        return new BaseCell(init_state);
    }

    public void removeCALayers(){
        // TODO: confirm that this properly disposes of layers:
        for (CALayer layerKey : ca_layers.keySet()){
            ca_layers.remove(layerKey);
        }
    }

    public CAGridBase getLayer(CALayer layer){
        return ca_layers.get(layer);
    }
}
