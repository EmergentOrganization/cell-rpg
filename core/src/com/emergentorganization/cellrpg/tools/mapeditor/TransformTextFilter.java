package com.emergentorganization.cellrpg.tools.mapeditor;

import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * Created by BrianErikson on 6/19/2015.
 */
public class TransformTextFilter implements VisTextField.TextFieldFilter {
    @Override
    public boolean acceptChar(VisTextField textField, char c) {
        if (c == '.') return true;
        try {
            //noinspection ResultOfMethodCallIgnored
            Float.parseFloat(""+c);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
