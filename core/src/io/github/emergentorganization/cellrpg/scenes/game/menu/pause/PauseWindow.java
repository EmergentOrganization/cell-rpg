package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PauseWindow extends VisWindow {
    private final Logger logger = LogManager.getLogger(getClass());

    private final SettingsMenu settingsMenu;
    private final DebugMenu debugMenu;
    private final EquipmentMenu equipmentMenu;

    public PauseWindow(final PixelonTransmission pt, final Stage stage, final World world) {
        super("", false);

        logger.debug("enter pause menu");

        VisTable table = new VisTable();
        this.setFillParent(false);
        this.centerWindow();
        this.add(table);
        this.clearListeners();

        // TODO: add buttons only if game is in proper state (ie no map before collecting item, no debug menu unless
        //          debug enabled, special settings for main menu, etc). 

        VisTextButton map = new VisTextButton("map(N/A)");
        map.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info("opened map setting");
            }
        });
        table.add(map).pad(0f, 0f, 5f, 0f).fill(true, false).row();


        settingsMenu = new SettingsMenu(table, stage, "settings", world);

        debugMenu = new DebugMenu(table, stage, "debug menu");

        equipmentMenu = new EquipmentMenu(pt, world, table, stage, "equipment menu");

        VisTextButton exit = new VisTextButton("exit to main menu");
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fadeOut();
                pt.gameOver(world);
            }
        });
        table.add(exit).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        table.align(Align.center);
        this.pack();
    }

    @Override
    public void fadeOut() {
        logger.info("closing pause window");
        super.fadeOut();
        if (settingsMenu != null)
            settingsMenu.closeSubmenu();
        if (debugMenu != null)
            debugMenu.closeSubmenu();
        if (equipmentMenu != null)
            equipmentMenu.closeSubmenu();
    }
}
