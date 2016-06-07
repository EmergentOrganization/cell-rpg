package io.github.emergentorganization.cellrpg.tools.menus;

/**
 * Created by orelb on 04-Jun-16.
 */
public class StringSetting {

    private final String label;
    private String value;

    public StringSetting(String label, String defaultValue) {
        this.label = label;
        this.value = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        this.value = val;
    }

    public String getLabel() {
        return label;
    }

}
