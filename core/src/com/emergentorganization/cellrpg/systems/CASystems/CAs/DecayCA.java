package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CASystems.CARenderSystem.CellRenderers.DecayCellRenderer;

/**
 * CA for visual effect which decays through states, allowing for a sequence of state colors to be drawn.
 * For example, a fading effect by using states which map to an array of colors with decreasing opacity.
 *
 * Created by 7yl4r on 12/16/2015.
 */
public class DecayCA implements iCA {
    public void generate(CAGridComponents gridComps) {
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                int state = gridComps.states[i][j].state;
                // stop decay at null color states
                if (DecayCellRenderer.stateColorMap[state] == null){
                    continue;
                } else {  // decay through color states
                    gridComps.states[i][j].state -= 1;
                }
            }
        }
    }
}
