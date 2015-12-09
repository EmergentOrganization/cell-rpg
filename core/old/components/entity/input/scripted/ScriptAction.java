package com.emergentorganization.cellrpg.components.entity.input.scripted;

/**
 * Created by OrelBitton on 09/06/2015.
 */
public class ScriptAction {

    protected ScriptedInputComponent input;

    public void init(ScriptedInputComponent input){}

    // restart the action here to make it usable again
    public void start(){}

    public void run(){}

    public boolean completed(){ return false; }

    public void setInputComponent(ScriptedInputComponent input){
        this.input = input;
    }

}
