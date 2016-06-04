package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import io.github.emergentorganization.cellrpg.tools.menus.AdjustableSetting;
import io.github.emergentorganization.cellrpg.tools.menus.MenuBuilder;
import io.github.emergentorganization.cellrpg.tools.menus.StringSetting;

import javax.swing.plaf.MenuBarUI;


public class GraphicsSettingsMenu extends Submenu {

    // screen type
    private StringSetting screenType = new StringSetting("Screen Type", "windowed");

    // screen size
    public AdjustableSetting screenW = new AdjustableSetting("width", 0, 400, 4000, 10);
    public AdjustableSetting screenH = new AdjustableSetting("height", 0, 400, 4000, 10);
    
    public GraphicsSettingsMenu(VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
    }

    public void addMenuTableButtons() {
        VisLabel settingLabel = new VisLabel("game may need reset after changing these...");
        menuTable.add(settingLabel).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        Preferences preferences = GameSettings.getPreferences();
        screenW.setValue(preferences.getInteger(GameSettings.KEY_GRAPHICS_WIDTH));
        screenH.setValue(preferences.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT));
        screenType.setValue(preferences.getString(GameSettings.KEY_GRAPHICS_TYPE));

        MenuBuilder.buildDropdownSetting(menuTable, menuWindow, new String[]{"windowed", "fullscreen-windowed", "fullscreen"}, screenType);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, screenW);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, screenH);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }

    @Override
    public void closeSubmenu() {
        Preferences preferences = GameSettings.getPreferences();
        preferences.putInteger(GameSettings.KEY_GRAPHICS_HEIGHT, (int) screenH.getValue());
        preferences.putInteger(GameSettings.KEY_GRAPHICS_WIDTH, (int) screenW.getValue());
        preferences.putString(GameSettings.KEY_GRAPHICS_TYPE, screenType.getValue());
        preferences.flush();

        super.closeSubmenu();
    }
}
