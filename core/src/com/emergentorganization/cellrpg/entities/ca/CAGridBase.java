package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * defines how to interact with a CAGrid.
 * Created by 7yl4r on 9/16/2015.
 */
public abstract class CAGridBase extends Entity {
    protected static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    protected static final int TIME_PER_GENERATION = 100;  // ms allocated per generation (approximate, actual is slightly longer)
    protected static final int TIME_PER_FOLLOW = 1000; // ms between checks between grid movements
    protected long timeToGenerate = TIME_PER_GENERATION;
    protected long lastFollowCheckTime = 0;

    public long generation = 0;

    protected final Logger logger = LogManager.getLogger(getClass());
    // size of grid in pixels
    protected int sx;
    protected int sy;

    // size of grid in cells
    protected int w;
    protected int h;

    // size of each cell
    protected int cellSize;

    // location of grid center
    protected float gridOriginX = 0;
    protected float gridOriginY = 0;

    protected CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.EMPTY;

    protected long lastGenerationTime = 0;
    protected BaseCell[][] states;
    protected Color[] stateColorMap;

    public CAGridBase(ZIndex z_index){
        super(z_index);
    }
    /*
        @z_index         : ZIndex level to render the grid
     */

    public abstract int getState(final float x, final float y);
    // returns state of cell nearest to given world-coordinates

    public abstract long stampState(final int[][] pattern, final int row, final int col);
    public abstract long stampState(final int[][] pattern, final int row, final int col, final BaseCell[][] cellStates);
    // stamps a pattern onto the grid at given row, col position

    public abstract long stampState(final int[][] pattern, final float x, final float y);
    public abstract long stampState(final int[][] pattern, Vector2 position);
    // stamps a pattern onto the grid centered at the nearest grid cells to the given world position

    protected abstract void reposition();
    // ensures proper positioning of grid as camera moves

    public long getGenerationNumber(){
        return generation;
    }

    public void resetGenerationNumber(){
        // resets generation counter
        generation = 0;
    }

    public int getSizeX(){
        // returns grid size (number of cells) in x-dimension
        return states.length;
    }

    public int getSizeY(){
        // returns grid size (number of cells) in y-dimension
        return states[0].length;
    }

    protected void generate(){
        // generates new state
        generation += 1;
    }

    /* === ENTITY METHODS (PROBABLY) OVERRIDDEN BY IMPLEMENTER === */



    @Override
    public void update(float deltaTime){
        // updates the grid. (probably) computes ca generations.
        super.update(deltaTime);

        if (!getScene().isEditor()) {
            long now = System.currentTimeMillis();
            if (now - lastGenerationTime > TIME_PER_GENERATION) {
                lastGenerationTime = now;
                generate();
                timeToGenerate = System.currentTimeMillis() - now;
                //logger.info("new cell generation computed. t=" + timeToGenerate + "ms");
            } else if (now - lastFollowCheckTime > TIME_PER_FOLLOW){
                lastFollowCheckTime = now;
                reposition(); // do this only in non-generation frames to even out computational requirements
            }
        }
    }

    @Override
    public void added(){
        super.added();
    }

    private float getXOrigin(Camera camera, float scale){
        return -OFF_SCREEN_PIXELS + gridOriginX - camera.position.x/scale;
    }
    private float getXOrigin(){
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        return getXOrigin(camera, scale);
    }
    private float getYOrigin(){
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        return getYOrigin(camera, scale);
    }
    private float getYOrigin(Camera camera, float scale){
        return -OFF_SCREEN_PIXELS + gridOriginY - camera.position.y/scale;
    }

    @Override
    public void debugRender(ShapeRenderer shapeRenderer) {
        super.debugRender(shapeRenderer);

        //NOTE: this is getting called... so maybe it's drawing in wrong location/scale?
        //shapeRenderer.setProjectionMatrix(new Matrix4());

        Gdx.gl.glEnable(GL20.GL_BLEND); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986
        Matrix4 oldMatrix = shapeRenderer.getProjectionMatrix();
        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        //long before = System.currentTimeMillis();
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        ShapeRenderer.ShapeType oldType = shapeRenderer.getCurrentType();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        float x;
        float y;

        // for setting origin (computed outside loop for efficiency++)
        float x_origin = getXOrigin(camera, scale);
        float y_origin = getYOrigin(camera, scale);

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                if (states[i][j].state != 0) {  // state must be > 0 else stateColorMap indexError
                    // draw square
                    shapeRenderer.setColor(stateColorMap[states[i][j].state-1]);

                    x = i * (cellSize + 1) + x_origin;  // +1 for cell border
                    y = j * (cellSize + 1) + y_origin;
                    shapeRenderer.rect(x, y, cellSize, cellSize);
                }
            }
        }
        shapeRenderer.set(oldType);
        shapeRenderer.setProjectionMatrix(oldMatrix);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        //logger.info("renderTime=" + (System.currentTimeMillis()-before));
    }
}
