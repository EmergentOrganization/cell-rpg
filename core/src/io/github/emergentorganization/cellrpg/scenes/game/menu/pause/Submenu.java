package io.github.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * submenu button included in a menu which launches a new menu when clicked.
 * new menu includes back button.
 */
class Submenu {
    private final Logger logger = LogManager.getLogger(getClass());
    VisWindow menuWindow;
    final Stage stage;
    VisTable menuTable;

    Submenu(VisTable table, final Stage stage, String buttonText) {
        this.stage = stage;

        VisTextButton submenuButton = new VisTextButton(buttonText);
        table.add(submenuButton).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        submenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                launchSubmenu();

                addBackButton();

                stage.addActor(menuWindow);
            }
        });
    }

    void launchSubmenu() {
        menuTable = new VisTable();
        menuWindow = new VisWindow("", false);
        menuWindow.setFillParent(false);
        menuWindow.centerWindow();
        menuWindow.add(menuTable).expand().fill();
        menuWindow.clearListeners();
    }

    public void closeSubmenu() {
        if (menuWindow != null) {
            menuWindow.fadeOut();
        }
    }

    void addBackButton() {
        VisTextButton back = new VisTextButton("<-back");
        menuTable.add(back).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                closeSubmenu();
                logger.debug("back out of sub-menu");
            }
        });

        menuTable.align(Align.center);
        menuWindow.pack();
    }
}
