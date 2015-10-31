package com.emergentorganization.cellrpg.tools.menus;

/**
 * Created by 7yl4r on 10/10/2015.
 */
public class AdjustableSetting {
    private String label;
    private float value;
    private float min;
    private float max;
    private float delta = 1;

    public AdjustableSetting(String name, float defaultVal, float MIN, float MAX, float deltaValue){
        this(name, defaultVal, MIN, MAX);
        delta = deltaValue;
    }
    public AdjustableSetting(String name, float defaultVal, float MIN, float MAX){
        label = name;
        value = defaultVal;
        min = MIN;
        max = MAX;
    }

    public void setValue(float newVal){
        value = newVal;
    }

    public float getDelta(){
        return delta;
    }

    public String getName(){
        return getLabel();
    }

    public String getLabel(){
        return label;
    }

    public float getValue(){
        return value;
    }

    public float getMin(){
        return min;
    }

    public float getMax(){
        return max;
    }
}
