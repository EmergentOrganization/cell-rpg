package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by tylar on 2015-07-06.
 */
public class CAGrid extends Entity {

    private static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid

    // size of grid in pixels
    private int sx;
    private int sy;

    // size of grid in cells
    private int w;
    private int h;

    // size of each cell
    private int cellSize;

    private int[][] states;
    private ShapeRenderer shapeRenderer;

    public CAGrid(int sizeOfCells) {
        /*
        :param cellSize: size level of grid
         */
        super();
        checkCellSize(sizeOfCells);

        cellSize = sizeOfCells;

        Camera camera = getScene().getGameCamera();
        sx = (int)camera.viewportWidth + OFF_SCREEN_PIXELS;
        sy = (int)camera.viewportHeight + OFF_SCREEN_PIXELS;

        w = sx / (cellSize + 1);  // +1 for border pixel between cells
        h = sy / (cellSize + 1);

        states = new int[w][h];

        // init states for testing (TODO: remove this after testing done, all init to 0)
        for (int i=0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                states[i][j] = Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
            }
        }
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!getScene().isEditor())
            updateView(deltaTime);
    }

    private void updateView(float deltaTime){
        // maintains grid around player while not computing on grid farther from player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);

        int x;
        int y;

        // TODO: pad leading edge of states with 0s, push existing states over, drop falling edge

        for (int i=0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++) {
                if (states[i][j] != 0) {
                    // draw square

                    // TODO: adjust position based on camera, move (lower left) corner into negative using OFF_SCREEN_PIXELS
                    x = i*(cellSize+1);  // +1 for cell border
                    y = j*(cellSize+1);
                    shapeRenderer.rect(x, y, cellSize, cellSize);
                }
            }
        }
        shapeRenderer.end();
    }

    private void checkCellSize(int size){
        // checks that given size is acceptable, else throws error
        if ( size == 1 ){
            return;
        } else {
            int acceptableSize = 3;
            while (acceptableSize <= size){
                if (size == acceptableSize){
                    return;
                } else {
                    acceptableSize = getNextSizeUp(acceptableSize);
                }
            } // else size not acceptable
            throw new UnsupportedOperationException("size must be in 1, 3, 11, 35...");
        }
    }

    private int getNextSizeUp(int lastSize){
        /*
         * available sizes assuming 1px border between cells given by
         * s(n) = 3(s(n-1))+2 for n > 1 (i.e. starting at s(2)=11)
         */
        if (lastSize < 3){
            throw new UnsupportedOperationException("previous cell size must be >= 3");
        } else {
            return 3*lastSize + 2;
        }
    }
}
