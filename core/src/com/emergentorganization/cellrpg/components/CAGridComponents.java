package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
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
    protected BaseCell[][] states;

    // location of grid center
    protected float gridOriginX = 0;
    protected float gridOriginY = 0;

    // size of each cell
    protected int cellSize;

    protected CAEdgeSpawnType edgeSpawner = CAEdgeSpawnType.EMPTY;

    public int getSizeX(){
        // returns grid size (number of cells) in x-dimension
        return states.length;
    }

    public int getSizeY(){
        // returns grid size (number of cells) in y-dimension
        return states[0].length;
    }

    protected BaseCell newCell(int init_state){
        return new BaseCell(init_state);
    }

}
