package com.emergentorganization.cellrpg.scenes.game.menu.pause;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by brian on 10/30/15.
 */
public class PauseWindow extends VisWindow {
    private final SettingsMenu settingsMenu;
    private final DebugMenu debugMenu;

    public PauseWindow(Stage stage) {
        super("", false);

        VisTable table = new VisTable();
        this.setFillParent(false);
        this.centerWindow();
        this.add(table);
        this.clearListeners();

        VisTextButton map = new VisTextButton("map(N/A)");
        map.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                System.out.println("opened map setting");
            }
        });
        table.add(map).pad(0f, 0f, 5f, 0f).fill(true, false).row();


        settingsMenu = new SettingsMenu(table, stage, "settings");

        debugMenu = new DebugMenu(table, stage, "debug menu");

        VisTextButton exit = new VisTextButton("exit to main menu");
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //CellRpg.fetch().setScreen(new MainMenu("disconnected. Dimensional hash saved."));
            }
        });
        table.add(exit).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        table.align(Align.center);
        this.pack();
    }
}