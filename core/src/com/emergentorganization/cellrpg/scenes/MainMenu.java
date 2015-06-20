package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by BrianErikson on 6/20/2015.
 */
public class MainMenu extends Scene {

    @Override
    public void create() {
        super.create();

        VisTable table = new VisTable();
        VisWindow window = new VisWindow("", false);
        window.setFillParent(true);
        window.add(table);

        VisTextButton play = new VisTextButton("Play");

        table.add(play).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        VisTextButton editor = new VisTextButton("Map Editor");
        table.add(editor);

        table.align(Align.center);

        getUiStage().addActor(window);

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                CellRpg.fetch().setScreen(new Test());
            }
        });

        editor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                CellRpg.fetch().setScreen(new MapEditor());
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        drawUI();
    }

    @Override
    public void hide() {

    }
}
