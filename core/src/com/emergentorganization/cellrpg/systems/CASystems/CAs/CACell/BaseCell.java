package com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by 7yl4r on 9/15/2015.
 */
public class BaseCell {
    private final Logger logger = LogManager.getLogger(getClass());

    public int state;  // cell state value
    public boolean lock; // TODO: true locks the cell, means "don't compute rule on this cell"

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
