package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by 7yl4r on 2015-11-18.
 */
@Wire
public class CARenderSystem extends BaseEntitySystem {

    protected static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid

    protected int cellSize;  // size of each cell

    // size of grid in pixels
    protected int sx;
    protected int sy;

    // size of grid in cells
    protected int w;
    protected int h;

    // location of grid center
    protected float gridOriginX = 0;
    protected float gridOriginY = 0;

    protected BaseCell[][] states;
    protected Color[] stateColorMap;

    private final ShapeRenderer renderer;  // TODO: change to ShapeRenderer?
    private final Logger logger = LogManager.getLogger(getClass());

    // === START BOILERPLATE

    // variables injected (by Artemis.World) @ runtime:
    private CameraSystem cameraSystem;
    private AssetManager assetManager;

    public CARenderSystem(ShapeRenderer shapeRenderer) {
        super(Aspect.all(CameraFollow.class));  // select only (assumed 1) camera-followed component (a bit hacky)

        this.renderer = shapeRenderer;

        int sizeOfCells = 11;
        checkCellSize(sizeOfCells);
        cellSize = sizeOfCells;

        stateColorMap = new Color[] {new Color(.3f,.5f,.7f,.5f), new Color(.7f, .5f, .3f, .5f)}; // TODO: testing only. remove when ready.
    }

    @Override
    protected  void begin() {
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  // clears the screen
        //batch.setProjectionMatrix(cameraSystem.getGameCamera().combined);
        //batch.begin();
    }

    @Override
    protected  void processSystem() {
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(cameraSystem.getGameCamera().combined);  // this should be uncommented, but doing so breaks cagrid...
        renderer.begin();

        Gdx.gl.glEnable(GL20.GL_BLEND); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986
        Matrix4 oldMatrix = renderer.getProjectionMatrix();
        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        //long before = System.currentTimeMillis();
        Camera camera = cameraSystem.getGameCamera();
        float scale = 1; // getScene().scale; ???
        ShapeRenderer.ShapeType oldType = renderer.getCurrentType();
        renderer.set(ShapeRenderer.ShapeType.Filled);

        // for setting origin (computed outside loop for efficiency++)
        float x_origin = getXOrigin(camera, scale);
        float y_origin = getYOrigin(camera, scale);

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                renderCell(i, j, renderer, x_origin, y_origin);
            }
        }
        renderer.set(oldType);
        renderer.setProjectionMatrix(oldMatrix);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        //logger.info("renderTime=" + (System.currentTimeMillis()-before));


//        for (Integer id : sortedEntityIds) {
//            process(id);
//        }
    }

    protected  void process(int entityId) {
        // process completed for each entity matching filter
//        Visual v = vm.get(entityId);
//        Position p = pm.get(entityId);
//        Scale s = sm.get(entityId);
//        Rotation r = rm.get(entityId);
//
//        TextureRegion t = assetManager.getCurrentRegion(v);
//        if (t != null) {
//            if (v.isAnimation) {
//                v.stateTime += world.getDelta();
//            }
//            batch.draw(t, cameraSystem.getGameCamera().position.x, cameraSystem.getGameCamera().position.y, 0, 0, t.getRegionWidth(), t.getRegionHeight(), s.scale, s.scale, r.angle);
//        }
    }

    @Override
    protected void end() {
        renderer.end();
    }

/* START UNVERIFIED BOILERPLATE METHODS <=============================================== */

    @Override
    protected  void inserted(int entityId) {
        Camera camera = cameraSystem.getGameCamera();
        float scale = 1; // getScene().scale;  ???
        sx = (int) (camera.viewportWidth / scale) + 2*OFF_SCREEN_PIXELS;
        sy = (int) (camera.viewportHeight / scale) + 2 * OFF_SCREEN_PIXELS;

        w = sx / (cellSize + 1);  // +1 for border pixel between cells
        h = sy / (cellSize + 1);

        logger.info("created CAGrid " + w + "(" + sx + "px)x" + h + "(" + sy + "px)");

        initStates();
        // TODO: initGenerationLoop();

    }

    @Override
    protected void removed(int entityId) {

    }

/* ===============================================> END UNVERIFIED BOILERPLATE*/

    protected void initStates(){
        w = 100;
        h = 100; // TODO: these just for testing. remove when ready.

        states = new BaseCell[w][h];
        // init states. ?required?
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j] = newCell(0);
            }
        }
        // init states for testing
        randomizeState();
    }

    private void randomizeState() {
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j].setState(Math.round(Math.round(Math.random())));// round twice? one is just a cast (I think)
            }
        }
    }

    protected BaseCell newCell(int init_state){
        return new BaseCell(init_state);
    }

    protected void renderCell(final int i, final int j, ShapeRenderer shapeRenderer,
                                       final float x_origin, final float y_origin){
        // just a basic implementation from NoBufferCAGrid
        // TODO: port the other ca render methods or find another way to support same functionality
        if (states[i][j].getState() != 0) {  // state must be > 0 else stateColorMap indexError
            // draw square
            shapeRenderer.setColor(stateColorMap[states[i][j].getState()-1]);

            float x = i * (cellSize + 1) + x_origin;  // +1 for cell border
            float y = j * (cellSize + 1) + y_origin;
            shapeRenderer.rect(x, y, cellSize, cellSize);
        }
    }

    private float getXOrigin(Camera camera, float scale){
        return -OFF_SCREEN_PIXELS + gridOriginX - camera.position.x/scale;
    }
    private float getXOrigin(){
        Camera camera = cameraSystem.getGameCamera();
        float scale = 1;  // getScene().scale; ???
        return getXOrigin(camera, scale);
    }
    private float getYOrigin(){
        Camera camera = cameraSystem.getGameCamera();
        float scale = 1;  // getScene().scale; ???
        return getYOrigin(camera, scale);
    }
    private float getYOrigin(Camera camera, float scale){
        return -OFF_SCREEN_PIXELS + gridOriginY - camera.position.y/scale;
    }

    private void checkCellSize(int size) {
        // checks that given size is acceptable, else throws error
        if (size == 1) {
            return;
        } else {
            int acceptableSize = 3;
            while (acceptableSize <= size) {
                if (size == acceptableSize) {
                    return;
                } else {
                    acceptableSize = getNextSizeUp(acceptableSize);
                }
            } // else size not acceptable
            throw new UnsupportedOperationException("size must be in 1, 3, 11, 35...");
        }
    }

    private int getNextSizeUp(int lastSize) {
        /*
         * available sizes assuming 1px border between cells given by
         * s(n) = 3(s(n-1))+2 for n > 1 (i.e. starting at s(2)=11)
         */
        if (lastSize < 3) {
            throw new UnsupportedOperationException("previous cell size must be >= 3");
        } else {
            return 3 * lastSize + 2;
        }
    }
}
