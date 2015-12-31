package com.emergentorganization.cellrpg.scenes.game.HUD;

import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by 7yl4r on 12/31/2015.
 */
public class ScoreDisplay {
    private World world;
    private Stage stage;
    private VisLabel scoreLabel;
    private int score = 0;

    public ScoreDisplay(World world, Stage stage){
        this.world = world;
        this.stage = stage;
        VisWindow scoreWindow = new VisWindow("", true);
        scoreWindow.setPosition(0, 1000, Align.topRight);

        VisTable tabl = new VisTable();
        scoreWindow.add(tabl);
        scoreLabel = new VisLabel(getFormattedScoreStr());
        tabl.add(scoreLabel);

        scoreWindow.pack();

        stage.addActor(scoreWindow);
    }

    public void updateScore(){
        // TODO: get entities with statsTracker component
        //world.getEntity(EntityID.PLAYER);

        scoreLabel.setText(getFormattedScoreStr());
        // scoreWindow.pack() repack? not needed as long as score stays same length.
    }

    private String getFormattedScoreStr(){
        String str = Integer.toString(score);
        return ("000000000" + str).substring(str.length());
    }
}
