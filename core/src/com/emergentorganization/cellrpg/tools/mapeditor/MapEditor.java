package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/**
 * Created by BrianErikson on 6/14/2015.
 */
public class MapEditor extends Scene {
    VisList<String> entityList;
    public static float LEFT_PANEL_HEIGHT = Gdx.graphics.getHeight();
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 5f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;

    @Override
    public void create() {
        super.create();

        VisUI.setDefaultTitleAlign(Align.center);
        setClearColor(new Vector3(1, 1, 1));

        initLeftPane();
        initMenuBar();
    }

    private void initMenuBar() {
        MenuBar menuBar = new MenuBar();
        Table table = menuBar.getTable();
        table.setWidth(MENU_BAR_WIDTH);
        table.setHeight(MENU_BAR_HEIGHT);
        table.setPosition(LEFT_PANEL_WIDTH, Gdx.graphics.getHeight() - MENU_BAR_HEIGHT);

        Menu modify = new Menu("Modify Object");
        modify.setSkin(VisUI.getSkin());
        menuBar.addMenu(modify);

        modify.addItem(new MenuItem("Translate", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Translate triggered");
            }
        }));

        modify.addItem(new MenuItem("Rotate", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Rotate triggered");
            }
        }));

        modify.addItem(new MenuItem("Scale", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Scale triggered");
            }
        }));

        getUiStage().addActor(menuBar.getTable());
    }

    private void initLeftPane() {
        entityList = new VisList<String>();
        entityList.setVisible(true);

        entityList.getItems().add("Test");
        entityList.getItems().add("Test2");
        entityList.getItems().add("Test3");
        entityList.getItems().add("Test4");
        entityList.getItems().add("Test5");
        entityList.getItems().add("Test6");
        entityList.getItems().add("Test7");
        entityList.getItems().add("Test8");
        entityList.getItems().add("Test9");
        entityList.getItems().add("Test10Longer");
        entityList.getItems().add("Tes0Loner");
        entityList.getItems().add("Test10Lnger");
        entityList.getItems().add("Test10ongr");
        entityList.getItems().add("Test10ger");
        entityList.getItems().add("Tes10Longer");
        entityList.getItems().add("Test10onger");
        entityList.getItems().add("Test10Loner");
        entityList.getItems().add("Test10Logr");
        entityList.getItems().add("Test10Lon");
        entityList.getItems().add("TestLoger");
        entityList.getItems().add("Test10Longe");
        entityList.getItems().add("Test10Longer");
        entityList.getItems().add("Tet10Longr");
        entityList.getItems().add("Test0Longer");


        VisScrollPane scrollPane = new VisScrollPane(entityList);
        scrollPane.setVisible(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setupFadeScrollBars(1f, 0.3f);
        scrollPane.setupOverscroll(20f, 30f, 200f);

        VisWindow leftPane = new VisWindow("Assets");
        leftPane.setWidth(LEFT_PANEL_WIDTH);
        leftPane.setHeight(LEFT_PANEL_HEIGHT);
        leftPane.clearListeners(); // disable interaction on window

        Cell<VisScrollPane> scrollCell = leftPane.add(scrollPane);
        scrollCell.align(Align.topLeft);
        getUiStage().addActor(leftPane);
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
