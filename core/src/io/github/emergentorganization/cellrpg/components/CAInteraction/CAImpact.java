package io.github.emergentorganization.cellrpg.components.CAInteraction;

import io.github.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Defines an impact as a state matrix stamp that is applied to a target ca grid.
 */
public class CAImpact {
    public int targetGridId;
    public int[][] impactStamp;

    private int interactionPeriod = 0;  // how many generations between CA interaction triggers
    private long lastInteractionGeneration = -1;

    public CAImpact(int _targetGridId, int[][] _impactStamp) {
        targetGridId = _targetGridId;
        impactStamp = _impactStamp;
    }

    public boolean readyToStamp(CAGridComponents caGridComps){
        // returns true if enough generations have passed to stamp again
        return caGridComps.generation - lastInteractionGeneration >= interactionPeriod;
    }

    public void impacted(CAGridComponents caGridComps){
        // should be called post-impact application to reset interaction generation timer
        lastInteractionGeneration = caGridComps.generation;
    }
}
