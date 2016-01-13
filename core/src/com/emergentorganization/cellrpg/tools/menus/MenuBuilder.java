package com.emergentorganization.cellrpg.tools.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;


public class MenuBuilder {
    public static void buildSliderSetting(VisTable menuTable, final VisWindow menuWindow, final AdjustableSetting setting) {
        VisLabel settingLabel = new VisLabel(setting.getLabel());
        menuTable.add(settingLabel).pad(0f, 0f, 5f, 0f).fill(true, false);

        final VisLabel settingValue = new VisLabel(Float.toString(setting.getValue()));
        menuTable.add(settingValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final VisSlider settingSlider =
                new VisSlider(setting.getMin(), setting.getMax(), setting.getDelta(), false);
        settingSlider.setValue(setting.getValue());
        settingSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setting.setValue(settingSlider.getValue());
                        settingValue.setText(Float.toString(setting.getValue()));
                        menuWindow.pack();
                    }
                }
        );
        menuTable.add(settingSlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();
    }
}
