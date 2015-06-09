package com.emergentorganization.cellrpg.components.input.scripted;

import com.emergentorganization.cellrpg.components.input.InputComponent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by OrelBitton on 08/06/2015.
 */
public class ScriptedInputComponent extends InputComponent {

    private HashMap<String, Script> scripts;
    private Script currentScript;

    public ScriptedInputComponent()
    {
        scripts = new HashMap<String, Script>();
    }

    public void registerScript(String name, Script script){
        if(scripts.containsKey(name))
            throw new RuntimeException("A script titled " + name + " is already registered.");

        scripts.put(name, script);
    }

    public void setScript(String name){
        if(!scripts.containsKey(name))
            throw new RuntimeException("A script titled " + name + " is not registered.");

        currentScript = scripts.get(name);
    }

    public Script getCurrentScript(){
        return currentScript;
    }

    public Script getScript(String name){
        if(!scripts.containsKey(name))
            return null;
        return scripts.get(name);
    }

    @Override
    public void update(float deltaTime) {
        if(currentScript == null)
            return;

       // TODO
    }
}
