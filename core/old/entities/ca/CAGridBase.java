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

import java.util.Timer;
import java.util.TimerTask;

/**
 * defines how to interact with a CAGrid.
 * Created by 7yl4r on 9/16/2015.
 */
public abstract class CAGridBase extends Entity {
    public static final long TIME_BTWN_GENERATIONS = 100;  // ms time in between generation() calls

    public long minGenTime = 999999;
    public long maxGenTime = 0;

    public long generation = 0;
    protected int stampCount = 0;


    protected CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.EMPTY;

    protected Color[] stateColorMap;

    public CAGridBase(int sizeOfCells, ZIndex z_index, Color[] state_color_map){
        /*
        @sizeOfCells     : display size of an individual cell
        @z_index         : ZIndex level to render the grid
        @state_color_map : list of colors which correspond to ca states  TODO: use a CAColorDefinition class instead
         */
        super(z_index);

    }

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

    public void scheduleGeneration(long runtime){
        // schedules a new generation thread
        Timer time = new Timer();
        // add runtime to TIME_BTWN_GENERATIONS to ensure this thread never uses more than 50% CPU time
        long genTime = TIME_BTWN_GENERATIONS + runtime;
        if( genTime > maxGenTime){
            maxGenTime = genTime;
        } else if (genTime < minGenTime){
            minGenTime = genTime;
        }
        time.schedule(new GenerateTask(this), genTime);
    }

    class GenerateTask extends TimerTask {
        private CAGridBase grid;

        public GenerateTask(CAGridBase _grid) {
            grid = _grid;
        }

        public void run() {
            try {
                long runtime = System.currentTimeMillis();
                grid.generate();
                grid.reposition();
                // schedule next update
                // NOTE: since generation computation time may be variable,
                //        this _could_ be scheduled before completing the above,
                //        but this also creates possibility of concurrency issues
                //        if generation time approaches TIME_BTWN_GENERATIONS.
                runtime = System.currentTimeMillis() - runtime;
                grid.scheduleGeneration(runtime);
                Thread.currentThread().stop();
                return;
            } catch (Exception ex) {
                logger.error("error running CA generation thread: " + ex.getMessage());
                Thread.currentThread().stop();
                return;
            }
        }
    }

    private void initGenerationLoop(){
        // sets up generation timer loop on separate thread
        scheduleGeneration(0);
    }

    @Override
    public void update(float deltaTime){
        // updates the grid. (probably) computes ca generations.
        super.update(deltaTime);
    }


    public int getCellSize(){
        return cellSize;
    }




    @Override
    public void dispose(){
        super.dispose();
    }


    private void checkSize(int size) {
        if (size % 2 < 1) {
            throw new UnsupportedOperationException("size must be odd!");
        } else {
            return;
        }
    }

    public abstract int getLastState(final int row, final int col);

    public int getState(final float x, final float y){
        // returns state of cell nearest to given world-coordinates
        int row = getIndexOfX(x);
        int col = getIndexOfY(y);
        return getState(row, col);
    }

    protected int getState(final int row, final int col) {
        // returns state of given location, returns 0 for out-of-bounds
        try {
            return _getState(row,col);
        } catch (IndexOutOfBoundsException err) {
            return 0;
        }
    }

    protected int _getState(final int row, final int col){
        // returns state, throws exception if out of bounds
        return states[row][col].getState();
    }

    protected void setState(final int row, final int col, final int newVal){
        states[row][col].setState(newVal);
    }

    private int[][] getNeighborhood(final int row, final int col, final int size) {
        // returns matrix of neighborhood around (row, col) with edge size "size"
        // size MUST be odd! (not checked for efficiency)

        // checkSize(size);
        int radius = (size - 1) / 2;
        int[][] neighbors = new int[size][size];
        for (int i = row - radius; i <= row + radius; i++) {
            int neighbor_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int neighbor_j = j - col + radius;
                neighbors[neighbor_i][neighbor_j] = getState(i, j);
            }
        }
        return neighbors;
    }

    public int getNeighborhoodSum(final int row, final int col, final int size) {
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
                    sum += getLastState(i, j);
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
                    sum += getState(i, j) * kernel[kernel_i][kernel_j];
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
                        sum += getState(i, j) * kernel[kernel_i][kernel_j][getState(i, j)];
                    } catch (IndexOutOfBoundsException err) {
                        throw new IndexOutOfBoundsException(
                                "kernel has no value for [" + kernel_i + "," + kernel_j + ","
                                + getState(i, j) + "]"
                        );
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

    protected int ca_rule(final int row, final int col) {
        // computes the rule at given row, col in cellStates array, returns result
        // Conway's Game of Life:
        switch (getNeighborhoodSum(row, col, 3)) {
            case 2:
                return getState(row, col);
            case 3:
                return 1;
            default:
                return 0;
        }
        // random state:
        //return Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
    }

    protected int getIndexOfX(float x){
        float scale = getScene().scale;
        float relative_x = x/scale-gridOriginX;
        return (int)relative_x/(cellSize+1) + w/2;
    }
    protected int getIndexOfY(float y){
        float scale = getScene().scale;
        float relative_y = y/scale-gridOriginY  ;
        int cell = (int)relative_y/(cellSize+1) + h/2;
        //System.out.println(y + " from " + gridOriginY + " nearest to " + cell);
        return cell;
    }

    /*
        === === === === ===
       CELL STATES INTERFACE
        === === === === ===
     */

    public long stampState(final int[][] pattern, Vector2 position){
        // stamps a pattern onto the grid centered at the nearest grid cells to the given world position
        int row = getIndexOfX(position.x);
        int col = getIndexOfY(position.y);
        //System.out.println("("+position.x + "," + position.y + ")==>(" + row + "," + col + ")");
        //center the pattern
        row -= pattern.length/2;
        col -= pattern[0].length/2;
        return stampState(pattern, row, col);
    }

    public long stampState(final int[][] pattern, final float x, final float y){
        return stampState(pattern, getIndexOfX(x), getIndexOfY(y));
    }

    public long stampState(final int[][] pattern, final int row, final int col) {
        // stamps a pattern onto the state with top-left corner @ (row, col)
        // returns estimated UNIX time when the pattern will be applied (@ next generation)
        if (       row > 0
                && col > 0
                && row < states.length-pattern.length
                && col < states[0].length-pattern[0].length) {

            // TODO: add pattern, row, col to queue which will be handled, call _stampState during next generation
            _stampState(pattern, row, col);
            stampCount++;
            return TIME_BTWN_GENERATIONS;  // TODO: estimate should + a few ms; currently this is soonest possible time.
        } else {
            //logger.warn("attempt to stamp pattern out of bounds (" + row + "," + col +"); error suppressed.");
            return -1;  // -1 indicates pattern not drawn, out-of-CAGrid bounds
        }
    }

    private void _stampState(final int[][] pattern, final int row, final int col) {
        // stamps pattern immediately into given cellStates
        //System.out.println("insert " + pattern.length + "x" + pattern[0].length + " pattern @ (" + row + "," + col + ")");
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                states[row + i][col + j].setState(pattern[i][j]);
            }
        }
    }

    protected void reposition(){
        gridFollow();
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


    private BaseCell getEdgeState(int x){
        // returns cell state in position x on a newly added edge
        int state;
        if (edgeSpawner == CAEdgeSpawnType.RANDOM_SPARSE) {
            state = getRandomState(0.05f);
        } else if ( edgeSpawner == CAEdgeSpawnType.RANDOM_50_50) {
            state = getRandomState(.5f);
        } else if ( edgeSpawner == CAEdgeSpawnType.RANDOM_DENSE){
            state = getRandomState(.8f);
        } else if (edgeSpawner == CAEdgeSpawnType.EMPTY){
            state = 0;
        } else {
            throw new IllegalStateException("edgeSpawn type not recognized");
        }
        return newCell(state);  // TODO: this should be constructor based on desired cell type
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
}
