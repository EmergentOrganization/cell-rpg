package com.emergentorganization.cellrpg.components.input.scripted;

import com.emergentorganization.cellrpg.components.input.InputComponent;

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

    @Override
    public void added() {
        super.added();

        for(Script s : scripts.values()){
            s.added(this);
        }
    }

    /**
     * Registers a script
     * @param name name
     * @param script the script
     */
    public void registerScript(String name, Script script){
        if(scripts.containsKey(name))
            throw new RuntimeException("A script named " + name + " is already registered.");

        scripts.put(name, script);
    }

    /**
     * Plays a registered script by its name
     * @param name script name
     */
    public void playScript(String name){
        if(!scripts.containsKey(name))
            throw new RuntimeException("A script named " + name + " is not registered.");

        currentScript = scripts.get(name);
    }

    /**
     *
     * @return the current playing script or null if no script is playing.
     */
    public Script getPlayingScript(){
        return currentScript;
    }

    /**
     *
     * @param name script name
     * @return a registered script by its name
     */
    public Script getScript(String name){
        if(!scripts.containsKey(name))
            return null;
        return scripts.get(name);
    }

    @Override
    public void update(float deltaTime) {
        if(currentScript == null)
            return;

        if(!currentScript.completed())
            currentScript.run();
        else if(currentScript.isLoop())
            currentScript.restart();
        else
            currentScript = null;
    }
}
