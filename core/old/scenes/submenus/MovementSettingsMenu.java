package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.widget.*;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class MovementSettingsMenu extends Submenu{
    public MovementSettingsMenu(VisTable table, Scene parent_scene, String buttonText){
        super(table, parent_scene, buttonText);
    }

    public void addMenuTableButtons(){
        // set up menu buttons:
        VisLabel controlTypeLabel = new VisLabel("control scheme");
        menuTable.add(controlTypeLabel).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        final PlayerInputComponent inComp = parentScene.getPlayer().getFirstComponentByType(PlayerInputComponent.class);
        final VisSelectBox controlsType = new VisSelectBox();
        menuTable.add(controlsType).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        controlsType.setItems(inComp.getInputTypeChoices());
        controlsType.setSelectedIndex(inComp.currentInputMethodIndex);
        controlsType.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // signal to movementControls new value to be set
                        inComp.setInputMethod(controlsType.getSelectedIndex());
                        // update UI display
                        // clear out old stuff
                        menuTable.clear();

                        // add back buttons
                        addMenuTableButtons();
                        addBackButton();

                        // resize window to fit new children
                        menuWindow.pack();
                    }
                }
        );

        inComp.addInputConfigButtons(menuTable, menuWindow);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }
}