package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by BrianErikson on 6/14/2015.
 */
public class MapEditor extends Scene {
    VisList<VisLabel> entityList;

    @Override
    public void create() {
        super.create();

        VisUI.setDefaultTitleAlign(Align.center);

        setClearColor(new Vector3(1, 0, 1));
        entityList = new VisList<VisLabel>();
        entityList.setVisible(true);

        VisLabel testLabel = new VisLabel();
        testLabel.setText("TEST"); // renders as :Test
        testLabel.setName("");  // wtf colon
        entityList.getItems().add(testLabel);

        VisScrollPane scrollPane = new VisScrollPane(entityList);
        scrollPane.setVisible(true);

        VisWindow leftPane = new VisWindow("Assets");
        leftPane.setWidth(Gdx.graphics.getWidth() / 5f);
        leftPane.setHeight(Gdx.graphics.getHeight());

        Cell<VisScrollPane> scrollCell = leftPane.add(scrollPane);
        scrollCell.align(Align.top);
        scrollCell.expand();
        getUiStage().addActor(leftPane);

        System.out.println("Create");
    }

    @Override
    public void show() {
        super.show();

        if (entityList == null)
            create();
    }

    @Override
    public void hide() {

    }
}
