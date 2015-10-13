package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class SettingsMenu extends Submenu{
    public MovementSettingsMenu moveMenu;
    public CameraSettingsMenu cameraMenu;

    public SettingsMenu(VisTable table, Scene parent_scene, String buttonText){
        super(table, parent_scene, buttonText);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

        // set up menu buttons:
        moveMenu = new MovementSettingsMenu(menuTable, parentScene, "controls");
        cameraMenu = new CameraSettingsMenu(menuTable, parentScene, "camera");

        // TODO:
        VisTextButton audio = new VisTextButton("audio(disabled)");
        menuTable.add(audio).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        audio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
    }

    @Override
    public void closeSubmenu(){
        super.closeSubmenu();
        if (moveMenu != null){
            moveMenu.closeSubmenu();
        }
        if (cameraMenu != null){
            cameraMenu.closeSubmenu();
        }
    }
}
