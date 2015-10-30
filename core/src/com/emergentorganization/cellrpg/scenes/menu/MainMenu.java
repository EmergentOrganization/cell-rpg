package com.emergentorganization.cellrpg.scenes.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by orelb on 10/30/2015.
 */
public class MainMenu extends BaseScene{

    private final float tableMargin;
    private Table table;

    public MainMenu(PixelonTransmission pt){
        super(pt);

        this.tableMargin = stage.getWidth() * 0.015f;
    }

    @Override
    public void show() {
        super.show();
        initUI();
    }

    private void initUI(){
        Skin s = pt.getUISkin();

        table = new Table(s);
        table.row();

        // title
        {
            Label title = new Label("Pixelon Transmission", s, "header");
            table.add(title).left().row();
        }

        // arcade
        {
            TextButton arcade = new TextButton("> Condemned Plainverse (Arcade Mode)", s);
            arcade.align(Align.left);
            arcade.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.GAME);
                }
            });

            table.add(arcade).left().row();
        }

        // story
        {
            TextButton story = new TextButton("> Stable Plainverse (Story Mode)", s);
            story.align(Align.left);
            story.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.GAME);
                }
            });

            table.add(story).left().row();
        }

        // lab
        {
            TextButton lab = new TextButton("> LifeGene Lab", s);
            lab.align(Align.left);
            lab.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.GAME);
                }
            });

            table.add(lab).left().row();
        }

        // Map Editor
        {
            TextButton editor = new TextButton("> Map Editor", s);
            editor.align(Align.left);
            editor.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.GAME);
                }
            });

            table.add(editor).left().row();
        }

        // settings
        {
            TextButton settings = new TextButton("> Settings", s);
            settings.align(Align.left);
            table.add(settings).left().row();
        }

        // quit
        {
            TextButton quit = new TextButton("> Quit", s);
            quit.align(Align.left);
            quit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });

            table.add(quit).left();
        }

        // pack and position table
        table.pack();
        table.setPosition(tableMargin, tableMargin);
        stage.addActor(table);

        // version label
        {
            Label versionInfo = new Label("v" + pt.getVersion(), s);
            versionInfo.pack();
            versionInfo.setPosition((stage.getWidth() - versionInfo.getWidth()) - tableMargin, tableMargin);

            stage.addActor(versionInfo);
        }
    }
}
