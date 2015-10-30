package com.emergentorganization.cellrpg.scenes.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.emergentorganization.cellrpg.scenes.Scene;

/**
 * Created by orelb on 10/30/2015.
 */
public class MainMenu extends BaseScene{

    public MainMenu(PixelonTransmission pt){
        super(pt);

    }

    @Override
    public void show() {
        super.show();
        initUI();
    }

    private void initUI(){
        Skin s = pt.getUISkin();

        // title
        {
            Label title = new Label("Cell-Rpg", s, "header");
            title.setPosition(90, 250);

            stage.addActor(title);
        }

        // arcade
        {
            TextButton arcade = new TextButton("> Arcade Mode", s);
            arcade.setPosition(90, 200);

            arcade.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.GAME);
                }
            });

            stage.addActor(arcade);
        }

        // settings
        {
            TextButton settings = new TextButton("> Settings", s);
            settings.setPosition(90, 160);

            stage.addActor(settings);
        }

        // quit
        {
            TextButton quit = new TextButton("> Quit", s);
            quit.setPosition(90, 120);

            quit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
            stage.addActor(quit);

        }

        // version label
        {
            Label versionInfo = new Label("v" + pt.getVersion(), s);
            versionInfo.setPosition(stage.getWidth() - 150, 0);

            stage.addActor(versionInfo);
        }
    }

}
