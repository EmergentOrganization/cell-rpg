//package com.emergentorganization.cellrpg.systems.CARenderSystem.CAGrid;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;
//import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.GeneticCell;
//import com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.GeneticCellBuilders.*;
//
//import java.util.ArrayList;
//
///**
// * Created by 7yl4r on 9/18/2015.
// */
//public class GeneticCAGrid extends CAGridBase{
//    private int lastBuilderStamp = 0;
//    private int selectedBuilder = 0;
//    // list of cells used to seed when stamps are placed:
//    private GeneticNetworkBuilderInterface[] builders = new GeneticNetworkBuilderInterface[]{
//            //new CellAlpha(),
//            new AgeDarkener(),
//            new MrBlue(),
//            new MrGreen(),
//            new MrRed()
//    };
//    public GeneticCAGrid(int sizeOfCells) {
//        /*
//        @sizeOfCells     : display size of an individual cell
//        @z_index         : ZIndex level to render the grid
//         */
//        super(sizeOfCells, new Color[]{});
//    }
//
//    @Override
//    public int getLastState(final int row, final int col){
//        try {
//            return states[row][col].state;
//        } catch (IndexOutOfBoundsException err){
//            return 0;  // if out-of-bounds, assume state=0
//        }
//    }
//
//    enum CellAction{
//        DIE,   // living cell -> dead cell
//        SPAWN, // dead  cell -> living cell
//        NONE  // no change to cell state
//    }
//
//    protected CellAction ca_rule(final int neighborCount) {
//        // Conway's Game of Life:
//        switch (neighborCount) {
//            case 2:
//                return CellAction.NONE;
//            case 3:
//                return CellAction.SPAWN;
//            default:
//                return CellAction.DIE;
//        }
//    }
//
//    @Override
//    protected void generate() {
//        super.generate();
//        // generates the next frame of the CA
//        for (int i = 0; i < states.length; i++) {
//            for (int j = 0; j < states[0].length; j++) {
//                GeneticCell cell = (GeneticCell) states[i][j];
//                cell.neighborCount = getNeighborhoodSum(i, j, 3);
//                states[i][j] = cell;
//            }
//        }
//
//        for (int i = 0; i < states.length; i++) {
//            for (int j = 0; j < states[0].length; j++) {
//                GeneticCell cell = (GeneticCell) states[i][j];
//                cell.dgrn.tick();
//                CellAction act = ca_rule(cell.neighborCount);
////                if (cell.state > 0) {
////                    System.out.println(cell.neighborCount + "->" + act.toString());
////                }
//                switch(act){
//                    case SPAWN:
//                        if (cell.state == 0 ){
//                            try {
//                                states[i][j] = new GeneticCell(1, getLiveParentsOf(i, j, 3)).incubate();
//                            } catch (IllegalStateException ex){
//                                // not enough parents
//                                states[i][j] = newCell(1);
//                            }
//                        }
//                        break;
//                    case DIE:
//                        setState(i, j, 0);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//    }
//
//    private ArrayList<GeneticCell> getLiveParentsOf(int row, int col, int neighborhoodSize){
//        // returns list of live cells surrounding cell
//        // size must be odd!
//        ArrayList<GeneticCell> parents = new ArrayList<GeneticCell>();
//        final boolean SKIP_SELF = true;
//
//        // checkSize(size);
//        final int radius = (neighborhoodSize - 1) / 2;
//        for (int i = row - radius; i <= row + radius; i++) {
//            for (int j = col - radius; j <= col + radius; j++) {
//                if (SKIP_SELF && i - row + radius == j - col + radius && i - row + radius == radius) {
//                    continue;
//                } else {
//                    try {
//                        GeneticCell cell = (GeneticCell) states[i][j];
//                        if (cell.state > 0) {
//                            parents.add(cell);
//                        }  // else cell is not alive, exclude
//                    } catch(ArrayIndexOutOfBoundsException err){
//                        // cell is on edge of grid, trying to access off-grid neighbor as potential parent
//                        // ignore
//                    }
//                }
//            }
//        }
//        return parents;
//    }
//
//    @Override
//    public void setState(int row, int col, int newState){
//        super.setState(row, col, newState);
//    }
//
//    private GeneticNetworkBuilderInterface getBuilder(){
//        // returns most appropriate cell builder
//        if (stampCount != lastBuilderStamp){
//            lastBuilderStamp = stampCount;
//            selectedBuilder++;
//            if (selectedBuilder >= builders.length){
//                selectedBuilder = 0;
//            }
//        }  // else return previously selected builder
//        return builders[selectedBuilder];
//    }
//
//    @Override
//    protected BaseCell newCell(int init_state){
//        return new GeneticCell(init_state, getBuilder()).incubate();
//    }
//
//    @Override
//    protected void renderCell(final int i, final int j, ShapeRenderer shapeRenderer,
//                              final float x_origin, final float y_origin){
//        if (getState(i,j) != 0) {  // state must be > 0 else stateColorMap indexError
//            // draw square
//            GeneticCell cell = (GeneticCell) states[i][j];
//            shapeRenderer.setColor(cell.getColor());
//
//            float x = i * (cellSize + 1) + x_origin;  // +1 for cell border
//            float y = j * (cellSize + 1) + y_origin;
//            shapeRenderer.rect(x, y, cellSize, cellSize);
//        }
//    }
//}