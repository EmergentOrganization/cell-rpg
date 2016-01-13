package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCell;

import java.util.ArrayList;


public class GeneticCA implements iCA {
    private enum CellAction{
        DIE,   // living cell -> dead cell
        SPAWN, // dead  cell -> living cell
        NONE  // no change to cell state
    }

    private CellAction ca_rule(final int neighborCount) {
        // Conway's Game of Life:
        switch (neighborCount) {
            case 2:
                return CellAction.NONE;
            case 3:
                return CellAction.SPAWN;
            default:
                return CellAction.DIE;
        }
    }

    private ArrayList<GeneticCell> getLiveParentsOf(int row, int col, int neighborhoodSize, CAGridComponents gridComps){
        // returns list of live cells surrounding cell
        // size must be odd!
        ArrayList<GeneticCell> parents = new ArrayList<GeneticCell>();
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (neighborhoodSize - 1) / 2;
        for (int i = row - radius; i <= row + radius; i++) {
            for (int j = col - radius; j <= col + radius; j++) {
                if (SKIP_SELF && i - row + radius == j - col + radius && i - row + radius == radius) {
                    continue;
                } else {
                    try {
                        GeneticCell cell = (GeneticCell) gridComps.states[i][j];
                        if (cell.state > 0) {
                            parents.add(cell);
                        }  // else cell is not alive, exclude
                    } catch(ArrayIndexOutOfBoundsException err){
                        // cell is on edge of grid, trying to access off-grid neighbor as potential parent
                        // ignore
                    }
                }
            }
        }
        return parents;
    }

    public void generate(CAGridComponents gridComps) {
        // count up all neighbors
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                GeneticCell cell = (GeneticCell) gridComps.states[i][j];
                cell.neighborCount = util.getNeighborhoodSum(i, j, 3, gridComps);
                gridComps.states[i][j] = cell;
            }
        }

        // act on neighbor counts
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                GeneticCell cell = (GeneticCell) gridComps.states[i][j];
                cell.dgrn.tick();
                CellAction act = ca_rule(cell.neighborCount);
//                if (cell.state > 0) {
//                    System.out.println(cell.neighborCount + "->" + act.toString());
//                }
                switch(act){
                    case SPAWN:
                        if (cell.state == 0 ){
                            try {
                                gridComps.states[i][j] = new GeneticCell(1, getLiveParentsOf(i, j, 3, gridComps)).incubate();
                            } catch (IllegalStateException ex){
                                // not enough parents
                                gridComps.states[i][j] = gridComps.newCell(1);
                            }
                        }
                        break;
                    case DIE:
                        gridComps.setState(i, j, 0);
                        break;
                    default:
                        break;
                }
            }
        }

        // change up the lastBuilder periodically. This adds some variety to the random spawns.
        gridComps.lastBuilderStamp -= 1;
    }
}
