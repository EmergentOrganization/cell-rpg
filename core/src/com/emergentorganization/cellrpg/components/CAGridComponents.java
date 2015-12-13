package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CAEdgeSpawnType;

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
    public static final long TIME_BTWN_GENERATIONS = 100;  // ms time in between generation() calls
    public static final int OFF_SCREEN_PIXELS = 200;  // number of pixels off screen edge to run CA grid
    public float SCALE = .025f;  // empirically derived constant... why is it this? idk...

    public BaseCell[][] states;

    // location of grid center
    public float gridOriginX = 0;
    public float gridOriginY = 0;

    // size of each cell
    public int cellSize = 3;

    public int cellCount = 0;  // number of live cells

    public CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.EMPTY;

    public Color[] stateColorMap;

    public long minGenTime = 999999;
    public long maxGenTime = 0;

    public long generation = 0;

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

    public int getLastState(final int row, final int col){
        // TODO: if renderType == noBuffer else if == genetic... etc
        return getLastState_noBuffer(row, col);
    }

    public int getLastState_noBuffer(final int row, final int col){
        // with no buffer there is no last state, just use current
        return getState(row, col);
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
        return new BaseCell(init_state);
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

    protected int _getState(final int row, final int col){
        // returns state, throws exception if out of bounds
        return states[row][col].getState();
    }

}
