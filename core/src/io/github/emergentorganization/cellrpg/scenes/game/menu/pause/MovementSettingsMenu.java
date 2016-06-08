package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.artemis.World;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.emergentorganization.cellrpg.input.player.PlayerInputProcessor;
import io.github.emergentorganization.cellrpg.core.systems.InputSystem;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;


public class MovementSettingsMenu extends Submenu {
    private final World world;

    public MovementSettingsMenu(VisTable table, Stage stage, String buttonText, World world) {
        super(table, stage, buttonText);
        this.world = world;
    }

    private void addMenuTableButtons() {
        // set up menu buttons:
        VisLabel controlTypeLabel = new VisLabel("weapon control scheme");
        menuTable.add(controlTypeLabel).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        PlayerInputProcessor playInProc = world.getSystem(InputSystem.class).getPlayerInputProcessor();
        final Preferences prefs = GameSettings.getPreferences();

        // weapon control buttons
        final VisSelectBox<String> weaponControlType = new VisSelectBox<String>();
        weaponControlType.setItems(playInProc.getWeaponCtrlChoices());
        menuTable.add(weaponControlType).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        weaponControlType.setSelectedIndex(prefs.getInteger(GameSettings.KEY_WEAPON_CONTROL_METHOD));
        weaponControlType.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // signal to movementControls new value to be set
                        prefs.putInteger(GameSettings.KEY_WEAPON_CONTROL_METHOD, weaponControlType.getSelectedIndex());
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

        // movement controls
        final VisSelectBox<String> moveControlType = new VisSelectBox<String>();
        moveControlType.setItems(playInProc.getMovementCtrlChoices());
        menuTable.add(moveControlType).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        moveControlType.setSelectedIndex(prefs.getInteger(GameSettings.KEY_MOVEMENT_CONTROL_METHOD));
        moveControlType.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // signal to movementControls new value to be set
                        prefs.putInteger(GameSettings.KEY_MOVEMENT_CONTROL_METHOD, moveControlType.getSelectedIndex());
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
        playInProc.getPlayerMovement().addInputConfigButtons(menuTable, menuWindow);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();
        addMenuTableButtons();
        menuWindow.pack();
    }
}
