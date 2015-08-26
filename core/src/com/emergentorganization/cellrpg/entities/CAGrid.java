package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by tylar on 2015-07-06.
 */
public class CAGrid extends Entity {
    private static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    private static final int TIME_PER_GENERATION = 100;  // ms allocated per generation (approximate, actual is slightly longer)
    private static final int TIME_PER_FOLLOW = 1000; // ms between checks between grid movements

    public long timeToGenerate = TIME_PER_GENERATION;
    private long lastFollowCheckTime = 0;

    public long generation = 0;

    private final Logger logger = LogManager.getLogger(getClass());

    // size of grid in pixels
    private int sx;
    private int sy;

    // size of grid in cells
    private int w;
    private int h;

    // size of each cell
    private int cellSize;

    // location of grid center
    private float gridOriginX = 0;
    private float gridOriginY = 0;

    private CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.EMPTY;

    private long lastGenerationTime = 0;
    private int[][] states;

    public CAGrid(int sizeOfCells, ZIndex z_index) {
        /*
        :param cellSize: size level of grid
         */
        super(z_index);
        checkCellSize(sizeOfCells);

        cellSize = sizeOfCells;
    }

    public int getSizeX(){
        return states.length;
    }

    public int getSizeY(){
        return states[0].length;
    }

    @Override
    public void update(float deltaTime) {
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
                gridFollow(); // do this only in non-generation frames to even out computational requirements
            }
        }
    }

    @Override
    public void added() {
        super.added();
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        sx = (int) (camera.viewportWidth / scale) + 2*OFF_SCREEN_PIXELS;
        sy = (int) (camera.viewportHeight / scale) + 2*OFF_SCREEN_PIXELS;

        w = sx / (cellSize + 1);  // +1 for border pixel between cells
        h = sy / (cellSize + 1);

        logger.info("created CAGrid " + w + "(" + sx + "px)x" + h + "(" + sy + "px)");

        states = new int[w][h];

        // init states for testing
        //randomizeState();
    }

    private void generate() {
        // generates the next frame of the CA
        int[][] stateBuffer = new int[states.length][states[0].length];
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                stateBuffer[i][j] = ca_rule(i, j, states);
            }
        }
        states = stateBuffer;
        generation += 1;
}

    /*
       ===========================================================
                  TEMPORARY METHODS FOR PLAYING WITH CA
       ============================================================
         These methods are here to create an easy-to-understand
         baseline against which to compare more efficient methods.
         These should probably not be used in production, as there
         are much more efficient ways of achieving the same results.
       ============================================================
     */


    private void checkSize(int size) {
        if (size % 2 < 1) {
            throw new UnsupportedOperationException("size must be odd!");
        } else {
            return;
        }
    }

    public int getState(final float x, final float y){
        int row = getIndexOfX(x);
        int col = getIndexOfX(y);
        return getState(row, col);
    }

    private int getState(final int row, final int col) {
        // returns state of given location, returns 0 for out-of-bounds
        return getState(row, col, states);
    }

    private int getState(final int row, final int col, final int[][] cellStates) {
        // returns state of given location, returns 0 for out-of-bounds
        try {
            return cellStates[row][col];
        } catch (IndexOutOfBoundsException err) {
            return 0;
        }
    }

    private int[][] getNeighborhood(final int row, final int col, final int size, final int[][] cellStates) {
        // returns matrix of neighborhood around (row, col) with edge size "size"
        // size MUST be odd! (not checked for efficiency)

        // checkSize(size);
        int radius = (size - 1) / 2;
        int[][] neighbors = new int[size][size];
        for (int i = row - radius; i <= row + radius; i++) {
            int neighbor_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int neighbor_j = j - col + radius;
                neighbors[neighbor_i][neighbor_j] = getState(i, j, cellStates);
            }
        }
        return neighbors;
    }

    private int getNeighborhoodSum(final int row, final int col, final int size, final int[][] cellStates) {
        // returns sum of all states in neighborhood
        // size MUST be odd! (not checked for efficiency)
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            for (int j = col - radius; j <= col + radius; j++) {
                if (SKIP_SELF && i - row + radius == j - col + radius && i - row + radius == radius) {
                    continue;
                } else {
                    sum += getState(i, j, cellStates);
                }
            }
        }
        return sum;
    }

    private float getKernelizedValue(final int[][] kernel, final int row, final int col, final int size, final int[][] cellStates) {
        // returns value from applying the kernel matrix to the given neighborhood
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            int kernel_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int kernel_j = j - row + radius;
                if (SKIP_SELF && kernel_i == kernel_j && kernel_i == radius) {
                    continue;
                } else {
                    sum += getState(i, j, cellStates) * kernel[kernel_i][kernel_j];
                }
            }
        }
        return sum;
    }

    private float get3dKernelizedValue(final int[][][] kernel, final int row, final int col, final int size, final int[][] cellStates) {
        // returns value from applying the kernel matrix to the given neighborhood
        // 3rd dimension of the kernel is state-space (ie, kernel[x][y][state])
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            int kernel_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int kernel_j = j - row + radius;
                if (SKIP_SELF && kernel_i == kernel_j && kernel_i == radius) {
                    continue;
                } else {
                    try {
                        sum += getState(i, j, cellStates) * kernel[kernel_i][kernel_j][getState(i, j, cellStates)];
                    } catch (IndexOutOfBoundsException err) {
                        throw new IndexOutOfBoundsException("kernel has no value for [" + kernel_i + "," + kernel_j + "," + getState(i, j, cellStates) + "]");
                    }
                }
            }
        }
        return sum;
    }

    /*
       ============================================================
                         END TEMPORARY CA METHODS
       ============================================================
     */

    private int ca_rule(final int row, final int col, final int[][] cellStates) {
        // computes the rule at given row, col in cellStates array, returns result

        // Conway's Game of Life:
        switch (getNeighborhoodSum(row, col, 3, cellStates)) {
            case 2:
                return cellStates[row][col];
            case 3:
                return 1;
            default:
                return 0;
        }

        // random state:
        //return Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
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
        //long before = System.currentTimeMillis();
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        shapeRenderer.setColor(0f, .8f, .5f, .4f); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986

        float x;
        float y;

        // for setting origin (computed outside loop for efficiency++)
        float x_origin = getXOrigin(camera, scale);
        float y_origin = getYOrigin(camera, scale);

        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                if (states[i][j] != 0) {
                    // draw square

                    x = i * (cellSize + 1) + x_origin;  // +1 for cell border
                    y = j * (cellSize + 1) + y_origin;
                    shapeRenderer.rect(x, y, cellSize, cellSize);
                }
            }
        }
        //logger.info("renderTime=" + (System.currentTimeMillis()-before));
    }

    private void gridFollow(){
        // enables grid to follow the camera
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;

        float dY = gridOriginY - camera.position.y/scale;
        //System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);

        while ( dY > cellSize+1){
            //System.out.println("BotAddRow");
            addRowBottom();
            dY = gridOriginY - camera.position.y/scale;
            //System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }
        while ( dY < -cellSize+1){
            //System.out.println("TopAddRow");
            addRowTop();
            dY = gridOriginY - camera.position.y/scale;
            //System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }


        float dX = gridOriginX - camera.position.x/scale;

        while ( dX > cellSize+1){
            addColLeft();
            dX = gridOriginX - camera.position.x/scale;
        }
        while ( dX < -cellSize+1){
            addColRight();
            dX = gridOriginX - camera.position.x/scale;
        }

    }

    private int getEdgeState(int x){
        // returns cell state in position x on a newly added edge
        if (edgeSpawner == CAEdgeSpawnType.RANDOM_SPARSE) {
            return getRandomState(0.05f);
        } else if ( edgeSpawner == CAEdgeSpawnType.RANDOM_50_50) {
            return getRandomState(.5f);
        } else if ( edgeSpawner == CAEdgeSpawnType.RANDOM_DENSE){
            return getRandomState(.8f);
        } else if (edgeSpawner == CAEdgeSpawnType.EMPTY){
            return 0;
        } else {
            throw new IllegalStateException("edgeSpawn type not recognized");
        }
    }

    private int getRandomState(float percentLive){
        if (Math.random() > percentLive) {
            return 0;
        } else {
            return 1;
        }
    }

    private void addColLeft(){
        // pushes a column onto left side
        for (int i = states.length-1; i >= 0; i--){
            for (int j = 0; j < states[0].length; j++){
                if ( i == 0 ){
                    states[i][j] = getEdgeState(j);
                } else {
                    states[i][j] = states[i-1][j];
                }
            }
        }
        gridOriginX -= cellSize+1;
    }

    private void addColRight(){
        // pushes a column onto right side
        for (int i = 0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                if ( i == states.length-1 ){
                    states[i][j] = getEdgeState(j);
                } else {
                    states[i][j] = states[i+1][j];
                }
            }
        }
        gridOriginX += cellSize+1;
    }

    private void addRowBottom(){
        // pushes a column of zeros
        for (int i = 0; i < states.length; i++){
            for (int j = states[0].length-1; j >= 0; j--){
                if ( j == 0 ){
                    states[i][j] = getEdgeState(i);
                } else {
                    states[i][j] = states[i][j-1];
                }
            }
        }
        gridOriginY -= cellSize+1;
    }

    private void addRowTop(){
        // pushes a column of zeros
        for (int i = 0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                if ( j == states[0].length-1 ){
                    states[i][j] = getEdgeState(i);
                } else {
                    states[i][j] = states[i][j+1];
                }
            }
        }
        gridOriginY += cellSize+1;
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

    private void randomizeState() {
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j] = Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
            }
        }
    }

    /*
        === === === === ===
       CELL STATES INTERFACE
        === === === === ===
     */

    private int getIndexOfX(float x){
        float scale = getScene().scale;
        float relative_x = x/scale-gridOriginX;
        return (int)relative_x/(cellSize+1) + w/2;
    }
    private int getIndexOfY(float y){
        float scale = getScene().scale;
        float relative_y = y/scale-gridOriginY  ;
        int cell = (int)relative_y/(cellSize+1) + h/2;
        //System.out.println(y + " from " + gridOriginY + " nearest to " + cell);
        return cell;
    }

    public long stampState(final int[][] pattern, Vector2 position){
        // stamps a pattern onto the grid at the nearest grid cells to the given world position
        int row = getIndexOfX(position.x);
        int col = getIndexOfY(position.y);
        //System.out.println("("+position.x + "," + position.y + ")==>(" + row + "," + col + ")");

        return stampState(pattern, row, col, states);
    }

    public long stampState(final int[][] pattern, final int row, final int col) {
        // stamps a pattern into specific grid location
        return stampState(pattern, row, col, states);
    }

    public long stampState(final int[][] pattern, final int row, final int col, final int[][] cellStates) {
        // stamps a pattern onto the state with top-left corner @ (row, col)
        // returns estimated UNIX time when the pattern will be applied (@ next generation)
        if (       row > 0
                && col > 0
                && row < states.length-pattern.length
                && col < states[0].length-pattern[0].length) {

            // TODO: add pattern, row, col to queue which will be handled, call _stampState during next generation
            _stampState(pattern, row, col, cellStates);

            return lastGenerationTime + TIME_PER_GENERATION;  // TODO: estimate should + a few ms; currently this is soonest possible time.

        } else {
            logger.warn("attempt to stamp pattern out of bounds (" + row + "," + col +"); error suppressed.");
            return -1;  // -1 indicates pattern not drawn, out-of-CAGrid bounds
        }
    }

    private void _stampState(final int[][] pattern, final int row, final int col, final int[][] cellStates) {
        // stamps pattern immediately into given cellStates
        //System.out.println("insert " + pattern.length + "x" + pattern[0].length + " pattern @ (" + row + "," + col + ")");
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                cellStates[row + i][col + j] = pattern[i][j];
            }
        }
    }
}