package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by 7yl4r on 9/15/2015.
 */
public class BaseCell {
    public int state;  // cell state value
    public int state2;
    // TODO: should store two state values here, one used for current, one used for previous.
    // for max efficiency, CAGrid should switch back and forth, copying one into the other.
    // or... should there be 2 BaseCell[][] arrays in CAGrid? no. that is a waste of space.
    //


    public boolean lock; // true locks the cell, means "don't compute rule on this cell"


    public BaseCell(int _state){
        state = _state;
    }
}
