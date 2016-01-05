package com.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.artemis.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.emergentorganization.cellrpg.input.player.PlayerInputProcessor;
import com.emergentorganization.cellrpg.systems.InputSystem;
import com.emergentorganization.cellrpg.tools.GameSettings;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Created by 7yl4r on 9/1/2015.
 */
public class MovementSettingsMenu extends Submenu {
    World world;
    public MovementSettingsMenu(VisTable table, Stage stage, String buttonText, World world) {
        super(table, stage, buttonText);
        this.world = world;
    }

    public void addMenuTableButtons(){
        // set up menu buttons:
        VisLabel controlTypeLabel = new VisLabel("weapon control scheme");
        menuTable.add(controlTypeLabel).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        final VisSelectBox controlsType = new VisSelectBox();
        PlayerInputProcessor playInProc = world.getSystem(InputSystem.class).getPlayerInputProcessor();
        controlsType.setItems(playInProc.getWeaponCtrlChoices());

        menuTable.add(controlsType).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        final Preferences prefs = GameSettings.getPreferences();

        controlsType.setSelectedIndex(prefs.getInteger( GameSettings.KEY_WEAPON_CONTROL_METHOD ));
        controlsType.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // signal to movementControls new value to be set
                        prefs.putInteger(GameSettings.KEY_WEAPON_CONTROL_METHOD, controlsType.getSelectedIndex());
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
        playInProc.getPlayerWeapon().addInputConfigButtons(menuTable, menuWindow);

        // TODO: movement controls
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }
}
