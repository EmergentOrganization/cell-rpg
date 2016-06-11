package io.github.emergentorganization.cellrpg.tools.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;


public class MenuBuilder {

    public static VisSlider buildSliderSetting(VisTable menuTable, final VisWindow menuWindow, final AdjustableSetting setting) {
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

        return settingSlider;
    }

    public static VisSelectBox<String> buildDropdownSetting(VisTable menuTable, final VisWindow menuWindow,
                                                            final String[] items, final StringSetting setting) {
        VisLabel settingLabel = new VisLabel(setting.getLabel());
        menuTable.add(settingLabel).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        final VisSelectBox<String> selectBox = new VisSelectBox<String>();
        selectBox.setItems(items);
        selectBox.setSelected(setting.getValue());
        menuTable.add(selectBox).pad(0, 0, 5f, 0).fill(true, false).row();
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.setValue(selectBox.getSelected());
            }
        });
        return selectBox;
    }

    public static void buildToggle() {

    }
}
