package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
public class ProfilerHUD extends Entity {
    private Stage stage;
    private VisWindow profilerWindow;

    private VisLabel entityCountLabel;
    private VisLabel FPSLabel;
    private VisLabel renderTimeLabel;
    private VisLabel memoryUsageLabel;
    private VisLabel xLabel;
    private VisLabel yLabel;

    private Runtime runtime = Runtime.getRuntime();


    public ProfilerHUD(){
        super(ZIndex.HUD);

    }

    @Override
    public void update(float deltaTime){
        entityCountLabel.setText(
                Integer.toString(getScene().getEntities().size())
        );
        FPSLabel.setText(
                Integer.toString(Gdx.graphics.getFramesPerSecond())
        );
        renderTimeLabel.setText(
                Long.toString(getScene().renderTime)
        );
        memoryUsageLabel.setText(
                Long.toString(runtime.totalMemory()/1024)
        );

        Vector3 pos = getScene().getGameCamera().position;
        xLabel.setText(Float.toString(pos.x));
        yLabel.setText(Float.toString(pos.y));

        profilerWindow.pack();
    }

    @Override
    public void added(){
        stage = getScene().getUiStage();
        profilerWindow = new VisWindow("", true);  // TODO: add window style
        profilerWindow.setPosition(1000f, 1000f, Align.topRight);  // TODO: add more clever positioning

        VisTable tabl = new VisTable();
        profilerWindow.add(tabl);

        entityCountLabel = new VisLabel(
                "???");
        tabl.add(entityCountLabel);
        tabl.add(new VisLabel(" entities in scene"));

        tabl.row();

        FPSLabel = new VisLabel("???");
        tabl.add(FPSLabel);
        tabl.add(new VisLabel("fps"));

        tabl.row();

        renderTimeLabel = new VisLabel("???");
        tabl.add(renderTimeLabel);
        tabl.add(new VisLabel("ms to render"));

        tabl.row();

        memoryUsageLabel = new VisLabel("?");
        tabl.add(memoryUsageLabel);
        tabl.add(new VisLabel("kb RAM taken"));

        tabl.row();

        tabl.add(new VisLabel("cam pos:"));
        xLabel = new VisLabel("?");
        tabl.add(xLabel);
        yLabel = new VisLabel("?");
        tabl.add(yLabel);


        stage.addActor(profilerWindow);
    }
}
