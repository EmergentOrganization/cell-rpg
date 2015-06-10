package com.emergentorganization.cellrpg.components.input.scripted.actions;

import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptAction;
import com.emergentorganization.cellrpg.components.input.scripted.ScriptedInputComponent;

import java.sql.Time;

/**
 * Created by OrelBitton on 10/06/2015.
 */
public class WaitAction extends ScriptAction{

    private float time; // in ms
    private long startingTime;
    private boolean completed;

    public WaitAction(float seconds){
        time = seconds * 1000;
    }

    @Override
    public void start() {
        startingTime = TimeUtils.millis();
        completed = false;
    }

    @Override
    public void run() {
        completed = TimeUtils.timeSinceMillis(startingTime) >= time;
    }

    @Override
    public boolean completed() {
        return completed;
    }
}
