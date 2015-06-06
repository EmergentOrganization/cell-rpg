package com.emergentorganization.cellrpg.components.messages;


import com.badlogic.gdx.math.Vector2;

/**
 * Created by OrelBitton on 07/06/2015.
 */
public class ArrivedToDestination extends BaseComponentMessage{

    public Vector2 dest;

    public ArrivedToDestination(Vector2 dest){
        this.dest = dest;
    }

}
