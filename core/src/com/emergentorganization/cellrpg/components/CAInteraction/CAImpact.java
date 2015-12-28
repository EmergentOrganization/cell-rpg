package com.emergentorganization.cellrpg.components.CAInteraction;

/**
 * Defines an impact as a state matrix stamp that is applied to a target ca grid.
 *
 * Created by 7yl4r on 12/18/2015.
 */
public class CAImpact {
    public int targetGridId;
    public int[][] impactStamp;

    public CAImpact(int _targetGridId, int[][] _impactStamp){
        targetGridId = _targetGridId;
        impactStamp = _impactStamp;
    }
}
