package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by tylar on 2015-07-06.
 */
public class CAGrid extends Entity {
    private static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    private static final int TIME_PER_GENERATION = 100;  // ms allocated per generation (approximate, actual is slightly longer)

    public long timeToGenerate = TIME_PER_GENERATION;

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

    private long lastGenerationTime = 0;
    private int[][] states;
    private ShapeRenderer shapeRenderer;  // maybe use sprite(texture(pixmap())) for better performance; see pixmap branch

    public CAGrid(int sizeOfCells, ZIndex z_index) {
        /*
        :param cellSize: size level of grid
         */
        super(z_index);
        checkCellSize(sizeOfCells);

        cellSize = sizeOfCells;

        shapeRenderer = new ShapeRenderer();
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

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        //long before = System.currentTimeMillis();
        Camera camera = getScene().getGameCamera();
        float scale = getScene().scale;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, .8f, .5f, .4f); // alpha only works if blend is toggled : http://stackoverflow.com/a/14721570/1483986

        gridFollow(scale, camera);

        float x;
        float y;

        // for setting origin (computed outside loop for efficiency++)
        float x_origin = -OFF_SCREEN_PIXELS + gridOriginX - camera.position.x/scale;
        float y_origin = -OFF_SCREEN_PIXELS + gridOriginY - camera.position.y/scale;

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
        shapeRenderer.end();
        //logger.info("renderTime=" + (System.currentTimeMillis()-before));
    }

    private void gridFollow(float scale, Camera camera){
        // enables grid to follow the camera
        float dY = gridOriginY - camera.position.y/scale;

        while ( dY > cellSize+1){
            System.out.println("BotAddRow");
            addRowBottom(scale);
            dY = gridOriginY - camera.position.y/scale;
            System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }
        while ( dY < -cellSize+1){
            System.out.println("TopAddRow");
            addRowTop(scale);
            dY = gridOriginY - camera.position.y/scale;
            System.out.println(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }

        float dX = gridOriginX - camera.position.x/scale;
    }

    private void addRowBottom(float scale){
        // pushes a column of zeros
        for (int i = 0; i < states.length; i++){
            for (int j = states[0].length-1; j >= 0; j--){
                if ( j == 0 ){
                    states[i][j] = 0;
                } else {
                    states[i][j] = states[i][j-1];
                }
            }
        }
        gridOriginY -= cellSize+1;
    }

    private void addRowTop(float scale){
        // pushes a column of zeros
        for (int i = 0; i < states.length; i++){
            for (int j = 0; j < states[0].length; j++){
                if ( j == states[0].length-1 ){
                    states[i][j] = 0;
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

    public long stampState(final int[][] pattern, final int row, final int col) {
        return stampState(pattern, row, col, states);
    }

    public long stampState(final int[][] pattern, final int row, final int col, final int[][] cellStates) {
        // stamps a pattern onto the state with top-left corner @ (row, col)
        // returns estimated UNIX time when the pattern will be applied (@ next generation)

        // TODO: add pattern, row, col to queue which will be handled, call _stampState during next generation
        _stampState(pattern, row, col, cellStates);

        return lastGenerationTime + TIME_PER_GENERATION;  // TODO: estimate should + a few ms; currently this is soonest possible time.
    }

    private void _stampState(final int[][] pattern, final int row, final int col, final int[][] cellStates) {
        // stamps pattern immediately into given cellStates
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                cellStates[row + i][col + j] = pattern[i][j];
            }
        }
    }
}