package com.emergentorganization.cellrpg.util;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Vector2;

import java.sql.Time;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * A utility class to record the screen coordinates with a delay between each "frame"
 * It uses a queue (FIFO) to maintain the coordinates in an ordered way.
 *
 * Created by OrelBitton on 07/06/2015.
 */
public class CoordinateRecorder {

    private float delay = 0; // Delay in ms
    private boolean a = false;
    private long lastRecord;

    /*
     * I chose queue because the complexity is O(1) for both insertion and removal.
     *
     * ArrayDeque is memory efficient than LinkedList since it does not have to keep an additional reference to the next node.
     */
    private Queue<Vector2> coords = new ArrayDeque<Vector2>();

    public CoordinateRecorder(float delay){
        this.delay = delay;
    }

    public void record(float x, float y){
        if(TimeUtils.timeSinceMillis(lastRecord) >= delay){
            lastRecord += delay;

            System.out.println("Recording " + x + ", " + y);
            coords.add(new Vector2(x, y));
        }
    }

    public boolean isEmpty(){
        return coords.isEmpty();
    }

    public Vector2 get(){
        if(coords.isEmpty())
            return null;

        return coords.remove();
    }

    public void clear(){
        while(!coords.isEmpty())
            coords.remove();
    }

    public void setDelay(float delay){
        this.delay = delay;
    }

    public float getDelay(){
        return delay;
    }

}
