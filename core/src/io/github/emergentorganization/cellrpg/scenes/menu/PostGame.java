package io.github.emergentorganization.cellrpg.scenes.menu;

import com.artemis.WorldConfiguration;
import com.artemis.managers.TagManager;
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
import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.scenes.game.WorldScene;
import io.github.emergentorganization.emergent2dcore.systems.WindowSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PostGame extends WorldScene {
    private final Logger logger = LogManager.getLogger(getClass());

    private final float tableMargin;
    private Skin skin;

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

        // add your score to pt.scores
        pt.scores.addScore(pt.playerScore);

        stage.clear();
        initUI();
    }

    private void initUI() {
        skin = pt.getUISkin();
        float cursorY = stage.getHeight() - tableMargin;  // cursor to keep track of where we are on the screen

        // title
        Label title = new Label("Planiverse Bridge Orb Connection Lost!", skin, "header");
        title.pack();
        title.setPosition(stage.getWidth() / 2, cursorY, Align.center);
        cursorY -= title.getHeight();

        stage.addActor(title);

        // menu table
        Table menuTable = makeMenuTable();
        stage.addActor(menuTable);

        // score rankings
        float scoreTableBottom = menuTable.getHeight() + tableMargin;
        Table scoreTable = makeScoreTable(cursorY, scoreTableBottom);
        stage.addActor(scoreTable);
    }

    @Override
    public WorldConfiguration getBaseWorldConfiguration() {
        WorldConfiguration wc = new WorldConfiguration();
        // TODO: set up postgame visuals
        return wc;
    }

    private Table makeScoreTable(float tableTop, float tableBottom){
        Table scoreTable = new Table(skin);
        scoreTable.row();

        float cursorY = tableTop;
        int MAX_SCORES_TO_CHECK = 9999;
        for(int rank = 0; rank < MAX_SCORES_TO_CHECK; rank++){  // aka while(true) with a backup plan
            logger.info(pt);
            logger.info(pt.scores);
            String username = pt.scores.getName(rank);
            int score = pt.scores.getScore(rank);

            // TODO: check if far enough down to skip scores (until we get near to player score)
            // if (cursorY < topScoresCutoff && rankIsNotNearEnoughToMyScore){
            //      skip;
            // } else {

            // TODO: fix spacing between columns
            String mark = "";
            if (pt.playerScore == score){
                mark = "-->";
            }
            scoreTable.add(new Label(mark + Integer.toString(rank+1) + " | ", skin)).right();
            scoreTable.add(new Label(username, skin)).left();
            Label scoreText = new Label( " | " + formatScore(score), skin);
            scoreTable.add(scoreText).left().row();
            cursorY -= scoreText.getHeight();

            if (cursorY < tableBottom){
                break;
            }
        }
        scoreTable.pack();
        scoreTable.setPosition(stage.getWidth()/2, tableTop, Align.top);
        return scoreTable;
    }

    private String formatScore(int score){
        // return rank formatted for display in table
        return String.format("%08d",score);
    }

    private Table makeMenuTable() {
        Table table = new Table(skin);
        table.row();
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
        table.pack();
        table.setPosition(tableMargin, tableMargin);
        return table;
    }
}
