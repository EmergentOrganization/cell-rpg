package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import io.github.emergentorganization.cellrpg.tools.menus.AdjustableSetting;
import io.github.emergentorganization.cellrpg.tools.menus.MenuBuilder;
import io.github.emergentorganization.cellrpg.tools.menus.StringSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GraphicsSettingsMenu extends Submenu {
    private final Logger logger = LogManager.getLogger(getClass());
    // screen size
    public AdjustableSetting screenW = new AdjustableSetting("width", 0, 400, 4000, 10);
    public AdjustableSetting screenH = new AdjustableSetting("height", 0, 400, 4000, 10);
    // screen type
    private StringSetting screenType = new StringSetting("Screen Type", "windowed");
    private VisLabel settingLabel = new VisLabel("The game must be restarted for these changes to take effect.");

    public GraphicsSettingsMenu(VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
    }

    public void addMenuTableButtons() {
        Preferences preferences = GameSettings.getPreferences();
        screenW.setValue(preferences.getInteger(GameSettings.KEY_GRAPHICS_WIDTH));
        screenH.setValue(preferences.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT));
        screenType.setValue(preferences.getString(GameSettings.KEY_GRAPHICS_TYPE));

        VisSelectBox<String> dropdownSetting = MenuBuilder.buildDropdownSetting(menuTable, menuWindow,
                new String[]{"windowed", "fullscreen-windowed", "fullscreen"}, screenType);
        VisSlider wSlider = MenuBuilder.buildSliderSetting(menuTable, menuWindow, screenW);
        VisSlider hSlider = MenuBuilder.buildSliderSetting(menuTable, menuWindow, screenH);

        settingLabel.setVisible(false);
        menuTable.add(settingLabel).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        dropdownSetting.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingLabel.setVisible(true);
            }
        });

        ChangeListener scrollListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingLabel.setVisible(true);
            }
        };

        wSlider.addListener(scrollListener);
        hSlider.addListener(scrollListener);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }

    @Override
    public void closeSubmenu() {
        logger.info("Saving graphics preferences...");
        Preferences preferences = GameSettings.getPreferences();
        preferences.putInteger(GameSettings.KEY_GRAPHICS_HEIGHT, (int) screenH.getValue());
        preferences.putInteger(GameSettings.KEY_GRAPHICS_WIDTH, (int) screenW.getValue());
        preferences.putString(GameSettings.KEY_GRAPHICS_TYPE, screenType.getValue());
        preferences.flush();
        logger.info("Graphics preferences saved.");

        settingLabel.setVisible(false);

        super.closeSubmenu();
    }
}
