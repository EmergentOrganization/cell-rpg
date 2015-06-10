package com.emergentorganization.cellrpg.components.input.scripted;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by OrelBitton on 09/06/2015.
 */
public class Script {

    private ArrayList<ScriptAction> actions;
    private int i = 0;
    private boolean loop = true;

    private boolean curScriptStarted = false;

    protected ScriptedInputComponent input;

    public Script(){
        this.actions = new ArrayList<ScriptAction>();
    }

    public Script(ScriptAction[] actions){
        this.actions = new ArrayList<ScriptAction>(Arrays.asList(actions));
    }

    public Script(ArrayList<ScriptAction> actions){
        this.actions = actions;
    }

    // used to resolve dependencies
    public void added(ScriptedInputComponent input){
        for(ScriptAction sa : actions)
            sa.init(input);
    }

    public void restart(){
        i = 0;
    }

    public void run(){
        if(actions.isEmpty())
            return;

        ScriptAction current = actions.get(i);

        if(!curScriptStarted)
        {
            curScriptStarted = true;
            current.start();
        }

        if(!current.completed())
        {
            current.run();
        }
        else {
            i++;
            curScriptStarted = false;
        }
    }

    public void addAction(ScriptAction action) {
        actions.add(action);
    }

    public boolean completed(){
        return i == actions.size();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

}
