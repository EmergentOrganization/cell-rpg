package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
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
    private VisList<EntityListNode> entityList;
    private EntityListNode selectedItem;
    
    private final Matrix3 scaler = new Matrix3();
    private final Matrix3 rotator = new Matrix3();
    private final Matrix3 translator = new Matrix3().scale(Scene.scale, Scene.scale);
    
    public static float LEFT_PANEL_HEIGHT = Gdx.graphics.getHeight();
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 5f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;

    @Override
    public void create() {
        super.create();
        setToEditor();

        VisUI.setDefaultTitleAlign(Align.center);

        initLeftPane();
        initMenuBar();

        getInputMultiplexer().addProcessor(new EditorInputProcessor(this));
    }

    private void initMenuBar() {
        MenuBar menuBar = new MenuBar();
        Table table = menuBar.getTable();
        table.setWidth(MENU_BAR_WIDTH);
        table.setHeight(MENU_BAR_HEIGHT);
        table.setPosition(LEFT_PANEL_WIDTH, Gdx.graphics.getHeight() - MENU_BAR_HEIGHT);

        Menu modify = new Menu("Modify");
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
        entityList = new VisList<EntityListNode>();
        entityList.setVisible(true);

        entityList.setItems(EntityList.get());
        selectedItem = entityList.getItems().get(entityList.getSelectedIndex());
        final VisList<EntityListNode> listRef = entityList;
        entityList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedItem = listRef.getItems().get(listRef.getSelectedIndex());
            }
        });

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

    public EntityListNode getSelectedItem() {
        return selectedItem;
    }
    
    public Matrix3 getNewObjectTransform() {
        return new Matrix3().mul(scaler).mul(rotator).mul(translator); // scale, rotate translate
    }
}
