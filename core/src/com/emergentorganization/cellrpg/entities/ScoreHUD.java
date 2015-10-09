package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.scenes.arcadeScore;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by tylar on 6/2/15.
 * used for NPCs & players
 */
public class ScoreHUD extends Entity {
    private Stage stage;
    private VisLabel scoreLabel;

    public ScoreHUD(){
        super(ZIndex.HUD);

    }

    private String getFormattedScoreStr(){
        Scene scene = getScene();
        int score = -1;
        if (scene instanceof arcadeScore){
            score = ((arcadeScore)scene).getScore();
        }

        String str = Integer.toString(score);
        return ("000000000" + str).substring(str.length());
    }

    @Override
    public void update(float deltaTime){
        scoreLabel.setText(getFormattedScoreStr());

        // scoreWindow.pack() repack? not needed as long as score stays same length.
    }

    @Override
    public void added(){
        stage = getScene().getUiStage();
        VisWindow scoreWindow = new VisWindow("", true);
        scoreWindow.setPosition(0, 1000, Align.topRight);

        VisTable tabl = new VisTable();
        scoreWindow.add(tabl);
        scoreLabel = new VisLabel(getFormattedScoreStr());
        tabl.add(scoreLabel);

        scoreWindow.pack();

        stage.addActor(scoreWindow);
    }
}
