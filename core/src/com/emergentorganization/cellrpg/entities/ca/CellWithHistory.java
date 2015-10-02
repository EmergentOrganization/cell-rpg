package com.emergentorganization.cellrpg.entities.ca;

/**
 * Created by 7yl4r on 9/25/2015.
 */
public class CellWithHistory extends BaseCell{
    // TODO: replace this with bufferSwapCell which should store two state values here,
    //       one used for current, one used for previous for max efficiency,
    //       CAGrid could switch back and forth, copying one into the other.
    protected int lastState;  // cell state value

    public CellWithHistory(int _state){
        super(_state);
        lastState = _state;
    }

    public int getLastState(){
        return lastState;
    }

    public void setLastState(final int newState){
        lastState = newState;
    }
}