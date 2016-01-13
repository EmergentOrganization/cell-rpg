package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.CellWithHistory;


public class BufferedCA implements  iCA{

    public void generate(CAGridComponents gridComps) {
        // generates the next frame of the CA
//        logger.info("gen buffered");
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                CellWithHistory cell = (CellWithHistory) gridComps.states[i][j];
                cell.setLastState(gridComps.states[i][j].getState());
            }
        }

        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                //System.out.print(i + "," + j +'\t');
                gridComps.states[i][j].setState(util.ca_rule(i, j, gridComps));
            }
        }
    }
}
