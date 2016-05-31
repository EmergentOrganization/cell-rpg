package io.github.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.kotcrab.vis.ui.widget.VisTextField;


public class TransformTextFilter implements VisTextField.TextFieldFilter {
    @Override
    public boolean acceptChar(VisTextField textField, char c) {
        if (c == '.') return true;
        try {
            //noinspection ResultOfMethodCallIgnored
            Float.parseFloat("" + c);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
