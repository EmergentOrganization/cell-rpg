package io.github.emergentorganization.cellrpg.scenes.menu;

import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.emergent2dcore.systems.WindowSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PostGame extends WorldScene {
    private final Logger logger = LogManager.getLogger(getClass());

    private final float tableMargin;
    private Table table;

    public PostGame(PixelonTransmission pt) {
        super(pt);

        this.tableMargin = stage.getWidth() * 0.015f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    @Override
    protected boolean shouldStash() {
        return true;
    }

    @Override
    public void show() {
        logger.trace("showing post-game menu");
        super.show();
        initUI();
    }

    private void initUI() {
        Skin skin = pt.getUISkin();

        table = new Table(skin);
        table.row();

        // title
        {
            Label title = new Label("Planiverse Bridge Orb Connection Lost!", skin, "header");
            title.pack();
            title.setPosition(stage.getWidth()/2, stage.getHeight() - tableMargin, Align.center);
//            versionInfo.setPosition((stage.getWidth() - versionInfo.getWidth()) - tableMargin, tableMargin);

            stage.addActor(title);
        }

        // score rankings
        {
            // TODO: get score from db or local file, show prev game score
            // TODO: align this top-center
            int rank = 1;
            String username = "player1";
            int score = 12345;
            Label tableRow = new Label(Integer.toString(rank) + "\t" + username + "\t" + Integer.toString(score), skin);
            table.add(tableRow).center().row();
        }

        // main menu
        {
            TextButton arcade = new TextButton("> main menu", skin);
            arcade.align(Align.left);
            arcade.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.MAIN_MENU);
                }
            });

            table.add(arcade).left().row();
        }
        // arcade
        {
            TextButton arcade = new TextButton("> replay", skin);
            arcade.align(Align.left);
            arcade.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.getSceneManager().setScene(Scene.ARCADE);
                }
            });

            table.add(arcade).left().row();
        }
        // quit
        {
            TextButton quit = new TextButton("> Quit", skin);
            quit.align(Align.left);
            quit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });

            table.add(quit).left().row();
        }

        // pack and position table
        table.pack();
        table.setPosition(tableMargin, tableMargin);
        stage.addActor(table);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration() {
        WorldConfiguration wc = new WorldConfiguration();
        // TODO: set up postgame visuals
        return wc;
    }

}
