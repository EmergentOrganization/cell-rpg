package com.emergentorganization.cellrpg.scenes.submenus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputType;
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
        PlayerInputType currentInputType = inComp.getInputMethod();
        final VisSelectBox controlsType = new VisSelectBox();
        menuTable.add(controlsType).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        controlsType.setItems(PlayerInputType.values());
        controlsType.setSelectedIndex(currentInputType.ordinal());
        controlsType.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // signal to movementControls new value to be set
                        inComp.setInputMethod((PlayerInputType) controlsType.getSelected());
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
                VisLabel walkDelayLabel = new VisLabel("walk delay");
                menuTable.add(walkDelayLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
                final VisLabel walkDelayValue = new VisLabel(Integer.toString(inComp.WALK_TIME));
                menuTable.add(walkDelayValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                final VisSlider walkDelaySlider = new VisSlider(inComp.WALK_TIME_MIN, inComp.WALK_TIME_MAX, 1, false);
                walkDelaySlider.setValue(inComp.WALK_TIME);
                walkDelaySlider.addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                inComp.WALK_TIME = (int) walkDelaySlider.getValue();
                                walkDelayValue.setText(Integer.toString(inComp.WALK_TIME));
                                menuWindow.pack();
                            }
                        }
                );
                menuTable.add(walkDelaySlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();

                VisLabel freeMoveLabel = new VisLabel("free-move radius");
                menuTable.add(freeMoveLabel).pad(0f, 0f, 5f, 0f).fill(true, false);
                final VisLabel freeMoveValue = new VisLabel(Integer.toString((int)inComp.FREE_MOVEMENT));
                menuTable.add(freeMoveValue).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                final VisSlider freeMoveSlider = new VisSlider(inComp.FREE_MOVEMENT_MIN, inComp.FREE_MOVEMENT_MAX, 1, false);
                freeMoveSlider.setValue(inComp.FREE_MOVEMENT);
                freeMoveSlider.addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                inComp.FREE_MOVEMENT = (int) freeMoveSlider.getValue();
                                freeMoveValue.setText(Integer.toString((int)inComp.FREE_MOVEMENT));
                                menuWindow.pack();
                            }
                        }
                );
                menuTable.add(freeMoveSlider).pad(0f, 0f, 5f, 0f).fill(true, false).row();

                break;
            case TELEPATHIC:
                VisTextButton teleTest = new VisTextButton("test telepathic control");
                menuTable.add(teleTest).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                final VisLabel testResult = new VisLabel("...");
                menuTable.add(testResult).pad(0f, 0f, 5f, 0f).fill(true, false).row();
                teleTest.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        testResult.setText("Test failed: no telepathic abilities detected.");
                        menuWindow.pack();
                    }
                });
                break;
        }
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }
}
