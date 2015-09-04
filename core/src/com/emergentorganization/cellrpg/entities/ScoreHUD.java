package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.emergentorganization.cellrpg.components.entity.GraphicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by tylar on 6/2/15.
 * used for NPCs & players
 */
public class ScoreHUD extends Entity {
    private Stage stage;
    private int score = 12345;

    public ScoreHUD(){
        super(ZIndex.HUD);

    }

    private String getFormattedScoreStr(){
        String str = Integer.toString(score);
        return ("000000000" + str).substring(str.length());
    }

    @Override
    public void added(){
        stage = getScene().getUiStage();
        VisWindow scoreWindow = new VisWindow("", true);
        VisTable tabl = new VisTable();
        scoreWindow.add(tabl);
        VisLabel scoreLabel = new VisLabel(getFormattedScoreStr());
        tabl.add(scoreLabel);
        stage.addActor(scoreWindow);
    }
}
