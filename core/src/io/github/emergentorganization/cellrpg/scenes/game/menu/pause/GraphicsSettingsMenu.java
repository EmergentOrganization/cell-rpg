package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import io.github.emergentorganization.cellrpg.tools.menus.AdjustableSetting;
import io.github.emergentorganization.cellrpg.tools.menus.MenuBuilder;


public class GraphicsSettingsMenu extends Submenu {
    // screen size
    public static AdjustableSetting screenW = new AdjustableSetting("width", 0, 400, 4000, 10);
    public static AdjustableSetting screenH = new AdjustableSetting("height", 0, 400, 4000, 10);


    private static final int PAD = 10;  // padding around default desktop window (non-fullscreen)

    public GraphicsSettingsMenu(VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
    }

    public void addMenuTableButtons() {
        VisLabel settingLabel = new VisLabel("game may need reset after changing these...");
        menuTable.add(settingLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
        
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
        preferences.flush();

        super.closeSubmenu();
    }
}
