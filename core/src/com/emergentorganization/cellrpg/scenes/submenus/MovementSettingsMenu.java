package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class MovementSettingsMenu extends Submenu{
    public MovementSettingsMenu(VisTable table, Scene parent_scene, String buttonText){
        super(table, parent_scene, buttonText);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

        // set up menu buttons:
        VisTextButton damageMe = new VisTextButton("optn1");
        menuTable.add(damageMe).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        damageMe.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //TODO
            }
        });

        VisTextButton audio = new VisTextButton("optn2");
        menuTable.add(audio).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        audio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                // TODO
            }
        });
    }
}
