package io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.core.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.CellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.iCellRenderer;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.BaseCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Renders cellular automata layers globally by selecting the entity with the CameraFollow component.
 */
@Wire
public class CARenderSystem extends BaseEntitySystem {

    private static final EnumMap<CellRenderer, iCellRenderer> cellRenderer = CellRenderer.getRendererMap();
    // list of entities registered w/ this system
    private final LinkedList<Integer> sortedEntityIds;
    private final ShapeRenderer renderer;
    private final Logger logger = LogManager.getLogger(getClass());
    // variables injected (by Artemis.World) @ runtime:
    private CameraSystem cameraSystem;
    private AssetManager assetManager;
    private ComponentMapper<CAGridComponents> CAComponent_m;
    private Matrix4 oldMatrix;
    private ShapeRenderer.ShapeType oldType;

    public CARenderSystem(ShapeRenderer shapeRenderer) {
        super(Aspect.all(CAGridComponents.class));

        this.renderer = shapeRenderer;
        sortedEntityIds = new LinkedList<Integer>();
    }

    @Override
    protected void begin() {
        renderer.setAutoShapeType(true);
        oldType = renderer.getCurrentType();
        oldMatrix = renderer.getProjectionMatrix();
        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.gl.glEnable(GL20.GL_BLEND); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986

        renderer.begin();
        renderer.set(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    protected void processSystem() {
        OrthographicCamera camera = (OrthographicCamera) cameraSystem.getGameCamera();
        for (Integer id : sortedEntityIds) {
            process(id, camera);
        }
    }

    private void process(int entityId, OrthographicCamera gameCamera) {
        CAGridComponents layerStuff = CAComponent_m.get(entityId);
        if (layerStuff.states != null) {
            logger.trace("rendering " + layerStuff.cellCount + " cells");
            renderGrid(layerStuff, gameCamera);
        } else {
            logger.warn("skipping render on null CA grid");
        }
    }

    @Override
    protected void end() {
        renderer.end();
        renderer.set(oldType);
        renderer.setProjectionMatrix(oldMatrix);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    protected void inserted(int entityId) {

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

    protected BaseCell newCell(int init_state) {
        return new BaseCell(init_state);
    }

    private void renderGrid(CAGridComponents ca_components, OrthographicCamera gameCamera) {

        float x_origin = ca_components.getXOrigin(gameCamera);
        float y_origin = ca_components.getYOrigin(gameCamera);
        iCellRenderer caRenderer = cellRenderer.get(ca_components.renderType);

        for (int i = 0; i < ca_components.states.length; i++) {
            for (int j = 0; j < ca_components.states[0].length; j++) {
                renderCell(ca_components, caRenderer, i, j, x_origin, y_origin);
            }
        }
    }

    private void renderCell(CAGridComponents layerComponents, iCellRenderer cellRenderer, final int i, final int j,
                            final float x_origin, final float y_origin) {
        cellRenderer.renderCell(renderer, layerComponents, i, j, x_origin, y_origin);
        // TODO: handle key not found exception. print "Renderer for renderType not found", default to ColorMap?
    }
}
