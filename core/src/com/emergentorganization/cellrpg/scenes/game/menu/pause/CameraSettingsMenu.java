package com.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.emergentorganization.cellrpg.tools.GameSettings;
import com.emergentorganization.cellrpg.tools.menus.AdjustableSetting;
import com.emergentorganization.cellrpg.tools.menus.MenuBuilder;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class CameraSettingsMenu extends Submenu{
    // min px between player & screen edge:
    public static AdjustableSetting edgeMargin = new AdjustableSetting("edge margin", 10, 1, 25, 1);

    // dist camera should try to lead player movement:
    public static AdjustableSetting cameraLead = new AdjustableSetting("camera-lead", 20, 1, 50, 1);

    // min distance between player & cam we care about (to reduce small-dist jitter & performance++):
    public static AdjustableSetting closeEnough = new AdjustableSetting("camera-player nearness cutoff", 4, 1, 30, 1);
    private Preferences preferences;

    public CameraSettingsMenu(VisTable table, Stage stage, String buttonText){
        super(table, stage, buttonText);
    }

    public void addMenuTableButtons(){
        // set up menu buttons:
        preferences = GameSettings.getPreferences();
        edgeMargin.setValue(preferences.getFloat(GameSettings.KEY_CAM_EDGE_MARGIN, 10));
        cameraLead.setValue(preferences.getFloat(GameSettings.KEY_CAM_LEAD, 20));
        closeEnough.setValue(preferences.getFloat(GameSettings.KEY_CAM_NEARNESS_CUTOFF, 4));

        MenuBuilder.buildSliderSetting(menuTable, menuWindow, edgeMargin);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, cameraLead);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, closeEnough);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }

    @Override
    public void closeSubmenu() {
        preferences.putFloat(GameSettings.KEY_CAM_EDGE_MARGIN, edgeMargin.getValue());
        preferences.putFloat(GameSettings.KEY_CAM_LEAD, cameraLead.getValue());
        preferences.putFloat(GameSettings.KEY_CAM_NEARNESS_CUTOFF, closeEnough.getValue());
        preferences.flush();

        super.closeSubmenu();
    }
}
