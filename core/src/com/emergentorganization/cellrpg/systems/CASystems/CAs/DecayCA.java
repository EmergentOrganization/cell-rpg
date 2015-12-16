package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Created by 7yl4r on 12/16/2015.
 */
public class DecayCA implements iCA {
    public void generate(CAGridComponents gridComps) {
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                gridComps.states[i][j].setState(gridComps.states[i][j].getState() - 1);
            }
        }
    }
}
