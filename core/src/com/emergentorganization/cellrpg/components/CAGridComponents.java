package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.systems.CASystems.CACell.BaseCell;
import com.emergentorganization.cellrpg.systems.CASystems.CACell.CellWithHistory;
import com.emergentorganization.cellrpg.systems.CASystems.CAEdgeSpawnType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This component contains a lot of information needed by a ca layer.
 * Much of these sub-components don't make much sense standing alone,
 * and it's not likely this will get used by anything other than the
 * CA grid layers. However, it may be better to separate these out
 * at a later date.
 *
 * Created by 7yl4r 2015-12-09.
 */

public class CAGridComponents extends Component {
    private final Logger logger = LogManager.getLogger(getClass());

    public static final long TIME_BTWN_GENERATIONS = 100;  // ms time in between generation() calls
    public static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    public float SCALE = .025f;  // empirically derived constant... why is it this? idk...

    // location of grid center TODO: maybe a position component could be used for this?
    public float gridOriginX = 0;
    public float gridOriginY = 0;

    public long minGenTime = 999999;
    public long maxGenTime = 0;

    public CellType cellType = CellType.BASE;
    public BaseCell[][] states;
    public int cellSize = 3;  // size of each cell [px]
    public int cellCount = 0;  // number of live cells
    public CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.RANDOM_SPARSE;
    public Color[] stateColorMap;
    public long generation = 0;
    public int stampCount = 0;

    public int getState(Vector2 pos){
        // returns state of cell nearest to given world-coordinates
//        logger.trace("getState(position)");
        int row = getIndexOfX(pos.x);
        int col = getIndexOfY(pos.y);
        return getState(row, col);
    }

    public int getState(final int row, final int col) {
        // returns state of given location, returns 0 for out-of-bounds
//        logger.trace("getState(int,int)");
        try {
            return _getState(row,col);
        } catch (IndexOutOfBoundsException err) {
            return 0;
        }
    }

    public int getLastState(final int row, final int col){
        switch(cellType){
            case WITH_HISTORY:
                return getLastState_buffered(row, col);
            case BASE:
            default:
                return getLastState_noBuffer(row, col);
        }
    }

    public int getLastState_noBuffer(final int row, final int col){
        // with no buffer there is no last state, just use current
        return getState(row, col);
    }

    public int getLastState_buffered(final int row, final int col){
        try {
            CellWithHistory cell = (CellWithHistory) states[row][col];
            return cell.getLastState();
        } catch (IndexOutOfBoundsException err){
            return 0;  // if out-of-bounds, assume state=0
        }
    }

    public int getSizeX(){
        // returns grid size (number of cells) in x-dimension
        return states.length;
    }

    public int getSizeY(){
        // returns grid size (number of cells) in y-dimension
        return states[0].length;
    }

    public float getXOrigin(Camera camera){
        return -OFF_SCREEN_PIXELS + gridOriginX - camera.position.x/SCALE;
    }
    public float getYOrigin(Camera camera){
        return -OFF_SCREEN_PIXELS + gridOriginY - camera.position.y/SCALE;
    }

    public BaseCell newCell(int init_state){
        switch (cellType){
            case WITH_HISTORY:
                return new CellWithHistory(init_state);
            //TODO: case GENETIC:
                //return new GeneticCell(init_state);
            case BASE:
            default:
                return new BaseCell(init_state);
        }
    }

    protected int getIndexOfX(float x){
        float relative_x = x/SCALE-gridOriginX;
        return (int)relative_x/(cellSize+1) + getSizeX()/2;
    }
    protected int getIndexOfY(float y){
        float relative_y = y/SCALE-gridOriginY  ;
        int cell = (int)relative_y/(cellSize+1) + getSizeY()/2;
        //System.out.println(y + " from " + gridOriginY + " nearest to " + cell);
        return cell;
    }

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
        if (       row > -1
                && col > -1
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

    public void setState(final int row, final int col, final int newVal){
        states[row][col].setState(newVal);
    }

    public String statesToString(int x, int y, int w, int h) {
        // returns string showing state of cells in given rect
        String res = "";
        for (int i = 0; i < w; i++){
            res += "{";
            for (int j = 0; j < h; j++){
                res += getState(i+x,j+y) + ",";
            }
            res += "}\n";
        }
        return res;
    }

    public String statesToString(){
        // returns string showing state of all cells
        return statesToString(0, 0, getSizeX(), getSizeY());
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

    protected int _getState(final int row, final int col){
        // returns state, throws exception if out of bounds
        return states[row][col].getState();
    }

    private void checkSize(int size) {
        if (size % 2 < 1) {
            throw new UnsupportedOperationException("size must be odd!");
        } else {
            return;
        }
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
