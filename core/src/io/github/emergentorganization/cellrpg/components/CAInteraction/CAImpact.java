package io.github.emergentorganization.cellrpg.components.CAInteraction;

/**
 * Defines an impact as a state matrix stamp that is applied to a target ca grid.
 */
public class CAImpact {
    public int targetGridId;
    public int[][] impactStamp;

    private int interactionPeriod = 1;  // how many generations between CA interaction triggers
    private int lastInteractionGeneration = -1;

    public CAImpact(int _targetGridId, int[][] _impactStamp) {
        targetGridId = _targetGridId;
        impactStamp = _impactStamp;
    }
}
