package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * A utility class to record the screen coordinates with a delay between each "frame"
 *
 * Created by OrelBitton on 07/06/2015.
 */
public class CoordinateRecorder {

    private float delay = 0; // Delay in ms
    private long lastRecord;

    private ArrayList<Vector2> coords = new ArrayList<Vector2>();

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

    public Vector2 getFirst(){
        if(coords.isEmpty())
            return null;

        Vector2 v = coords.get(0);
        coords.remove(0);

        return v;
    }

    public ArrayList<Vector2> getCoords(){
        return coords;
    }

    public void clear(){
        coords.clear();
    }

    public void setDelay(float delay){
        this.delay = delay;
    }

    public float getDelay(){
        return delay;
    }

}
