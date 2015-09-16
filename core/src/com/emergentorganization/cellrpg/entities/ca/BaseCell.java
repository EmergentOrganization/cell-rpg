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
    //


    public boolean lock; // true locks the cell, means "don't compute rule on this cell"


    public BaseCell(int _state){
        state = _state;
    }
}
