package io.github.emergentorganization.cellrpg.systems.CASystems.CAs;

import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.CellWithHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BufferedCA implements iCA {
    private final Logger logger = LogManager.getLogger(getClass());

    public void generate(CAGridComponents gridComps) {
        // generates the next frame of the CA
        logger.trace("gen buffered");
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                CellWithHistory cell = (CellWithHistory) gridComps.states[i][j];
                cell.setLastState(gridComps.states[i][j].getState());
            }
        }

        gridComps.cellCount = 0;
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                logger.trace(i + "," + j + '\t');
                int newState = util.ca_rule(i, j, gridComps);
                gridComps.states[i][j].setState(newState);
                if (newState != 0) {
                    gridComps.cellCount += 1;
                }
            }
        }
    }
}
