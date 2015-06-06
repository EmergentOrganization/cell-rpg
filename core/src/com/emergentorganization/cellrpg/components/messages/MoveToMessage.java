package com.emergentorganization.cellrpg.components.messages;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.BaseComponent;

/**
 * Created by OrelBitton on 04/06/2015.
 */
public class MoveToMessage extends BaseComponentMessage {

    // The position the entity shall move to.
    public Vector2 destination = new Vector2();

    public MoveToMessage(){}

    public void set(float x, float y){
       destination.set(x, y);
    }

}
