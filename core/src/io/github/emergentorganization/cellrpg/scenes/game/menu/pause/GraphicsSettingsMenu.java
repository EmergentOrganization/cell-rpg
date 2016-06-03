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
    public static AdjustableSetting screenW = new AdjustableSetting("width", getDefaultW(), 400, 4000, 10);
    public static AdjustableSetting screenH = new AdjustableSetting("height", getDefaultH(), 400, 4000, 10);

    public static final boolean FULLSCREEN_DEFAULT = false;

    private static final int PAD = 10;  // padding around default desktop window (non-fullscreen)

    public GraphicsSettingsMenu(VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
    }

    public void addMenuTableButtons() {
        VisLabel settingLabel = new VisLabel("game may need reset after changing these...");
        menuTable.add(settingLabel).pad(0f, 0f, 5f, 0f).fill(true, false);

        Preferences preferences = GameSettings.getPreferences();
        screenW.setValue(preferences.getInteger(GameSettings.KEY_GRAPHICS_WIDTH, getDefaultW()));
        screenH.setValue(preferences.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT, getDefaultH()));

        MenuBuilder.buildSliderSetting(menuTable, menuWindow, screenW);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, screenH);
    }

    public static int getDefaultW(){
        // returns default game width
        Preferences prefs = GameSettings.getPreferences();
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            boolean fullscr = prefs.getBoolean(GameSettings.KEY_GRAPHICS_FULLSCREEN, FULLSCREEN_DEFAULT);
            if (fullscr) {
                return Gdx.graphics.getDisplayMode().width;
            } else {  // windowed defaults to full-screen minus some padding
                return Gdx.graphics.getDisplayMode().width - PAD*2;
            }
        } else {
            return 600;
        }
    }

    public static int getDefaultH(){
        // returns default game height
        Preferences prefs = GameSettings.getPreferences();
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            boolean fullscr = prefs.getBoolean(GameSettings.KEY_GRAPHICS_FULLSCREEN, FULLSCREEN_DEFAULT);
            if (fullscr) {
                return Gdx.graphics.getDisplayMode().height;
            } else {  // windowed defaults to full-screen minus some padding
                return Gdx.graphics.getDisplayMode().height - PAD*2;
            }
        } else {
            return 400;
        }
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
