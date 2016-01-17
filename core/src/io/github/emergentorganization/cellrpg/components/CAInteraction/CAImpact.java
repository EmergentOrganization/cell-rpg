package io.github.emergentorganization.cellrpg.components.CAInteraction;

/**
 * Defines an impact as a state matrix stamp that is applied to a target ca grid.
 */
public class CAImpact {
    public int targetGridId;
    public int[][] impactStamp;

    public CAImpact(int _targetGridId, int[][] _impactStamp) {
        targetGridId = _targetGridId;
        impactStamp = _impactStamp;
    }
}
