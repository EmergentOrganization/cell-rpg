package com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell;


public class CellWithHistory extends BaseCell {
    // TODO: replace this with bufferSwapCell which should store two state values here,
    //       one used for current, one used for previous for max efficiency,
    //       CAGrid could switch back and forth, copying one into the other.
    protected int lastState;  // cell state value

    public CellWithHistory(int _state) {
        super(_state);
        lastState = _state;
    }

    public int getLastState() {
        return lastState;
    }

    public void setLastState(final int newState) {
        lastState = newState;
    }

    public void setState(final int newState) {
//        lastState = state;
        state = newState;
    }
}
