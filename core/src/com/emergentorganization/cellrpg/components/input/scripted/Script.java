package com.emergentorganization.cellrpg.components.input.scripted;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by OrelBitton on 09/06/2015.
 */
public class Script {

    private ArrayList<ScriptAction> actions;
    private int i = 0;

    private boolean loop = false;

    protected ScriptedInputComponent input;

    // TODO
    // private ArrayList<ScriptAction> insertion = new ArrayList<>();
    // private ArrayList<ScriptAction> removal = new ArrayList<>();

    public Script(){
        this.actions = new ArrayList<ScriptAction>();
    }

    public Script(ScriptAction[] actions){
        this.actions = new ArrayList<ScriptAction>(Arrays.asList(actions));
    }

    public Script(ArrayList<ScriptAction> actions){
        this.actions = actions;
    }

    public void added(){};

    public void run(){
        if(actions.isEmpty())
            return;

        ScriptAction current = actions.get(i);

        if(!current.completed())
            current.run();
        else
            getNext();
    }

    public void setLoop(boolean loop){
        this.loop = loop;
    }

    public boolean isLoop(){
        return loop;
    }

    protected void getNext(){
        // TODO

        if(i == actions.size() - 1){

        }
    }

    public void setInputComponent(ScriptedInputComponent input){
        this.input = input;
    }

}
