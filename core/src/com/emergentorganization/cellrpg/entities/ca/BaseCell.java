package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by 7yl4r on 9/15/2015.
 */
public class BaseCell {
    protected int state;  // cell state value
    public boolean lock; // true locks the cell, means "don't compute rule on this cell"

    public BaseCell(int _state){
        state = _state;
    }

    public int getState(){
        return state;
    }
    public void setState(final int new_state){
        state = new_state;
    }
}
