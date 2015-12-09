package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;
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

    // artemis-injected entity components:
    private ComponentMapper<CAGridComponents> CAComponent_m;

    // list of entities registered w/ this system
    private final LinkedList<Integer> sortedEntityIds;

    private final ShapeRenderer renderer;
    private final Logger logger = LogManager.getLogger(getClass());

    // variables injected (by Artemis.World) @ runtime:
    private ComponentMapper<Position> pm;
    private CameraSystem cameraSystem;
    private AssetManager assetManager;

    public CARenderSystem(ShapeRenderer shapeRenderer) {
        super(Aspect.all(CAGridComponents.class));

        this.renderer = shapeRenderer;
        sortedEntityIds = new LinkedList<Integer>();
    }

    // TODO: where do I add layer entities?
//    LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS).added(camera);
//    LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS_GENETIC).added(camera);
//    LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS_MEGA).added(camera);
//    LayerBuilder.addVyroidLayer(ca_layers, CALayer.VYROIDS_MINI).added(camera);
//    LayerBuilder.addVyroidLayer(ca_layers, CALayer.ENERGY).added(camera);

    @Override
    protected  void begin() {
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(cameraSystem.getGameCamera().combined);  // this should be uncommented, but doing so breaks cagrid...
        renderer.begin();
    }

    @Override
    protected  void processSystem() {
        for (Integer id : sortedEntityIds) {
            process(id);
        }
    }

    protected  void process(int entityId) {
        CAGridComponents layerStuff = CAComponent_m.get(entityId);
        //CALayer layerKey = entry.getKey();
        //CAGridBase layer = entry.getValue();

        renderGrid();
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

    public void renderGrid() {
        Camera camera = cameraSystem.getGameCamera();

        float x_origin = getXOrigin(camera);
        float y_origin = getYOrigin(camera);

        //shapeRenderer.setProjectionMatrix(new Matrix4());
        Gdx.gl.glEnable(GL20.GL_BLEND); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986
        Matrix4 oldMatrix = renderer.getProjectionMatrix();
        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        //long before = System.currentTimeMillis();
        ShapeRenderer.ShapeType oldType = renderer.getCurrentType();
        renderer.set(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                renderCell(i, j, renderer, x_origin, y_origin);
            }
        }
        renderer.set(oldType);
        renderer.setProjectionMatrix(oldMatrix);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        //logger.info("renderTime=" + (System.currentTimeMillis()-before));
    }

    protected abstract void renderCell(final int i, final int j, ShapeRenderer shapeRenderer,
                                       final float x_origin, final float y_origin);

}
