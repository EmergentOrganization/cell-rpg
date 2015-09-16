package com.emergentorganization.cellrpg.entities.ca;

/**
 * Created by 7yl4r on 9/15/2015.
 */
public interface BaseCell {
    public int getState();
    public void setState(int newState);

    /*
    @lastStates : CAGrid which contains last cell state grid accessible via CAGrid.getState(i, j)
    @i : first dimensional location coord of this cell in the given lastStates grid
    @j : 2nd dimensional location coord of this cell in the given lastStates grid
     */
    public void generate(CAGrid lastStates, int i, int j);

}
