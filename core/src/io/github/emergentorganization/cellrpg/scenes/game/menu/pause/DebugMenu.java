package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.emergentorganization.cellrpg.tools.GameSettings;


public class DebugMenu extends Submenu {
    public DebugMenu(VisTable table, Stage stage, String buttonText) {
        super(table, stage, buttonText);
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

        final Preferences prefs = GameSettings.getPreferences();
        final String prefix = "devMode:";

        final VisTextButton devMode = new VisTextButton(prefix + GameSettings.devMode());
        menuTable.add(devMode).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        devMode.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                prefs.putBoolean(GameSettings.KEY_DEV_DEVMODE, !GameSettings.devMode());
                devMode.setText(prefix + GameSettings.devMode());
            }
        });
    }
}
