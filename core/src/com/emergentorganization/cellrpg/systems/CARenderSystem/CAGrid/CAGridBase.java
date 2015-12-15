package com.emergentorganization.cellrpg.systems.CARenderSystem.CAGrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CAEdgeSpawnType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * defines how to interact with a CAGrid.
 * Created by 7yl4r on 9/16/2015.
 */
public abstract class CAGridBase {
//
//
//
//    // size of grid in cells NOTE: use getSizeX & getSizeY instead!
//    protected int w;
//    protected int h;
//
//
//
//    public CAGridBase(int sizeOfCells, Color[] state_color_map){
//        /*
//        @sizeOfCells     : display size of an individual cell
//        @z_index         : ZIndex level to render the grid
//        @state_color_map : list of colors which correspond to ca states  TODO: use a CAColorDefinition class instead
//         */
//        checkCellSize(sizeOfCells);
//        stateColorMap = state_color_map;
//
//        cellSize = sizeOfCells;
//    }
//
//    public long getGenerationNumber(){
//        return generation;
//    }
//
//    public void resetGenerationNumber(){
//        // resets generation counter
//        generation = 0;
//    }
//
//    public void update(float deltaTime){
//        // updates the grid. (probably) computes ca generations.
//    }
//
//    public int getCellSize(){
//        return cellSize;
//    }
//
//    public void dispose(){
//
//    }
//
//    private void checkSize(int size) {
//        if (size % 2 < 1) {
//            throw new UnsupportedOperationException("size must be odd!");
//        } else {
//            return;
//        }
//    }
//
//    private void checkCellSize(int size) {
//        // checks that given size is acceptable, else throws error
//        if (size == 1) {
//            return;
//        } else {
//            int acceptableSize = 3;
//            while (acceptableSize <= size) {
//                if (size == acceptableSize) {
//                    return;
//                } else {
//                    acceptableSize = getNextSizeUp(acceptableSize);
//                }
//            } // else size not acceptable
//            throw new UnsupportedOperationException("size must be in 1, 3, 11, 35...");
//        }
//    }
//
//    private int getNextSizeUp(int lastSize) {
//        /*
//         * available sizes assuming 1px border between cells given by
//         * s(n) = 3(s(n-1))+2 for n > 1 (i.e. starting at s(2)=11)
//         */
//        if (lastSize < 3) {
//            throw new UnsupportedOperationException("previous cell size must be >= 3");
//        } else {
//            return 3 * lastSize + 2;
//        }
//    }
//
//    public abstract int getLastState(final int row, final int col);
//
//
//    protected void setState(final int row, final int col, final int newVal){
//        states[row][col].setState(newVal);
//    }
//
//    private int[][] getNeighborhood(final int row, final int col, final int size) {
//        // returns matrix of neighborhood around (row, col) with edge size "size"
//        // size MUST be odd! (not checked for efficiency)
//
//        // checkSize(size);
//        int radius = (size - 1) / 2;
//        int[][] neighbors = new int[size][size];
//        for (int i = row - radius; i <= row + radius; i++) {
//            int neighbor_i = i - row + radius;
//            for (int j = col - radius; j <= col + radius; j++) {
//                int neighbor_j = j - col + radius;
//                neighbors[neighbor_i][neighbor_j] = getState(i, j);
//            }
//        }
//        return neighbors;
//    }
//

//
//    /*
//       ============================================================
//                         END TEMPORARY CA METHODS
//       ============================================================
//     */
//
//
//    protected int getIndexOfX(float x){
//        float relative_x = x/SCALE-gridOriginX;
//        return (int)relative_x/(cellSize+1) + w/2;
//    }
//    protected int getIndexOfY(float y){
//        float relative_y = y/SCALE-gridOriginY  ;
//        int cell = (int)relative_y/(cellSize+1) + h/2;
//        //System.out.println(y + " from " + gridOriginY + " nearest to " + cell);
//        return cell;
//    }
//
//    /*
//        === === === === ===
//       CELL STATES INTERFACE
//        === === === === ===
//     */
//
//

}
