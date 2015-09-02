package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * submenu button included in a menu which launches a new menu when clicked.
 * new menu includes back button.
 *
 * Created by 7yl4r on 9/1/2015.
 */
public class Submenu {
    protected VisWindow menuWindow;
    protected Scene parentScene;
    protected VisTable menuTable;

    public Submenu(VisTable table, Scene parent_scene, String buttonText){
        parentScene = parent_scene;

        VisTextButton submenuButton = new VisTextButton(buttonText);
        table.add(submenuButton).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        submenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                launchSubmenu();

                VisTextButton back = new VisTextButton("<-back");
                menuTable.add(back).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                back.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        closeSubmenu();
                        //System.out.println("back out of sub-menu");
                    }
                });

                menuTable.align(Align.center);
                parentScene.getUiStage().addActor(menuWindow);
            }
        });
    }

    public void launchSubmenu(){
        menuTable = new VisTable();
        menuWindow = new VisWindow("", false);
        menuWindow.setFillParent(false);
        menuWindow.centerWindow();
        menuWindow.add(menuTable);
        menuWindow.clearListeners();
    }

    public void closeSubmenu(){
        if (menuWindow != null) {
            menuWindow.fadeOut();
        }
    }
}
