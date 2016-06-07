package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;


public class SettingsMenu extends Submenu {

    World world;
    private CameraSettingsMenu cameraMenu;
    private MovementSettingsMenu moveMenu;
    private GraphicsSettingsMenu graphicsMenu;

    public SettingsMenu(VisTable table, Stage stage, String buttonText, World _world) {
        super(table, stage, buttonText);
        world = _world;
    }

    @Override
    public void launchSubmenu() {
        super.launchSubmenu();

        // set up menu buttons:
        moveMenu = new MovementSettingsMenu(menuTable, stage, "controls", world);
        cameraMenu = new CameraSettingsMenu(menuTable, stage, "camera");
        graphicsMenu = new GraphicsSettingsMenu(menuTable, stage, "graphics");

        // TODO: audio settings
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
    public void closeSubmenu() {
        super.closeSubmenu();
        if (moveMenu != null) {
            moveMenu.closeSubmenu();
        }
        if (cameraMenu != null) {
            cameraMenu.closeSubmenu();
        }
        if (graphicsMenu != null) {
            graphicsMenu.closeSubmenu();
        }
    }
}
