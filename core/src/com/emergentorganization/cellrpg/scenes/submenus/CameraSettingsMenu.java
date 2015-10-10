package com.emergentorganization.cellrpg.scenes.submenus;

import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.menus.MenuBuilder;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class CameraSettingsMenu extends Submenu{
    public CameraSettingsMenu(VisTable table, Scene parent_scene, String buttonText){
        super(table, parent_scene, buttonText);
    }

    public void addMenuTableButtons(){
        // set up menu buttons:
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, FollowingCamera.edgeMargin);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, FollowingCamera.cameraLead);
        MenuBuilder.buildSliderSetting(menuTable, menuWindow, FollowingCamera.closeEnough);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }
}
