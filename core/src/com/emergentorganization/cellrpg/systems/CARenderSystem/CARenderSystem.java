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

    // variables injected (by Artemis.World) @ runtime:
    private CameraSystem cameraSystem;
    private AssetManager assetManager;
    private ComponentMapper<CAGridComponents> CAComponent_m;

    // list of entities registered w/ this system
    private final LinkedList<Integer> sortedEntityIds;

    private final ShapeRenderer renderer;
    private final Logger logger = LogManager.getLogger(getClass());

    public CARenderSystem(ShapeRenderer shapeRenderer) {
        super(Aspect.all(CAGridComponents.class));

        this.renderer = shapeRenderer;
        sortedEntityIds = new LinkedList<Integer>();
    }

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

        if (layerStuff.states != null) {
            //logger.info("rendering " + layerStuff.cellCount + " cells");
            renderGrid(layerStuff);
        } else {
            logger.info("skipping render on null CA grid");
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

    public void renderGrid(CAGridComponents ca_components) {
        Camera camera = cameraSystem.getGameCamera();

        float x_origin = ca_components.getXOrigin(camera);
        float y_origin = ca_components.getYOrigin(camera);

        //shapeRenderer.setProjectionMatrix(new Matrix4());
        Gdx.gl.glEnable(GL20.GL_BLEND); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986
        Matrix4 oldMatrix = renderer.getProjectionMatrix();
        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        //long before = System.currentTimeMillis();
        ShapeRenderer.ShapeType oldType = renderer.getCurrentType();
        renderer.set(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < ca_components.states.length; i++) {
            for (int j = 0; j < ca_components.states[0].length; j++) {
                renderCell(ca_components, i, j, x_origin, y_origin);
            }
        }
        renderer.set(oldType);
        renderer.setProjectionMatrix(oldMatrix);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        //logger.info("renderTime=" + (System.currentTimeMillis()-before));
    }

    protected void renderCell(CAGridComponents layerComponents, final int i, final int j,
                              final float x_origin, final float y_origin){
        // TODO: if layerComponents.renderType == normal else if == genetic... etc
        renderCell_normal(layerComponents, i, j, x_origin, y_origin);
    }

    protected void renderCell_normal(CAGridComponents layerComponents, final int i, final int j,
                              final float x_origin, final float y_origin){
        if (layerComponents.states[i][j].getState() != 0) {  // state must be > 0 else stateColorMap indexError
            // draw square
            renderer.setColor(layerComponents.stateColorMap[layerComponents.states[i][j].getState()-1]);

            float x = i * (layerComponents.cellSize + 1) + x_origin;  // +1 for cell border
            float y = j * (layerComponents.cellSize + 1) + y_origin;
            renderer.rect(x, y, layerComponents.cellSize, layerComponents.cellSize);
        }
    }
}
