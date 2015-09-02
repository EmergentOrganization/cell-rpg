package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emergentorganization.cellrpg.components.entity.ShieldComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputType;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

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

        PlayerInputType currentInputType = parentScene.getPlayer().getFirstComponentByType(PlayerInputComponent.class).getInputMethod();
        final VisSelectBox controlsType = new VisSelectBox();
        menuTable.add(controlsType).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        controlsType.setItems(PlayerInputType.values());
        controlsType.setSelectedIndex(currentInputType.ordinal());
        controlsType.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // signal to movementControls new value to be set
                        parentScene.getPlayer().getFirstComponentByType(PlayerInputComponent.class)
                                .setInputMethod((PlayerInputType) controlsType.getSelected());
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

        switch(currentInputType){
            case MOUSE:
                VisTextButton mouseCtrl = new VisTextButton("mouse-ctrl-optn");
                menuTable.add(mouseCtrl).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                mouseCtrl.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        // TODO
                    }
                });
                break;
            case TELEPATHIC:
                VisTextButton teleTest = new VisTextButton("test-telepathic-control");
                menuTable.add(teleTest).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                teleTest.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        // TODO: display something like "no telepathic abilities detected"
                    }
                });
                break;
        }


    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
    }
}
