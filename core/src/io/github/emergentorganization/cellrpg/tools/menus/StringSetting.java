package io.github.emergentorganization.cellrpg.tools.menus;

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
