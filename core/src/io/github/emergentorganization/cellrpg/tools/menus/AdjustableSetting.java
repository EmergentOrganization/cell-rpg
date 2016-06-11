package io.github.emergentorganization.cellrpg.tools.menus;


public class AdjustableSetting {
    /*
    Game Setting with numerical value that falls in between two bounds and with granularity set by delta.
     */
    private final String label;
    private float value;
    private final float min;
    private final float max;
    private float delta = 1;

    public AdjustableSetting(String name, float defaultVal, float MIN, float MAX, float deltaValue) {
        this(name, defaultVal, MIN, MAX);
        delta = deltaValue;
    }

    private AdjustableSetting(String name, float defaultVal, float MIN, float MAX) {
        label = name;
        value = defaultVal;
        min = MIN;
        max = MAX;
    }

    public float getDelta() {
        return delta;
    }

    public String getName() {
        return getLabel();
    }

    public String getLabel() {
        return label;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float newVal) {
        value = newVal;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }
}
