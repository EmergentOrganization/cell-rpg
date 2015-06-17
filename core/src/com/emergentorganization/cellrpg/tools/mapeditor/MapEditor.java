package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/**
 * Created by BrianErikson on 6/14/2015.
 */
public class MapEditor extends Scene {
    private VisList<EntityListNode> entityList;
    private EntityListNode selectedItem;
    
    private final Matrix3 scaler = new Matrix3().setToScaling(Scene.scale, Scene.scale);
    private final Matrix3 rotator = new Matrix3();
    private final Matrix3 translator = new Matrix3();
    
    public static float LEFT_PANEL_HEIGHT = Gdx.graphics.getHeight();
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 5f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;
    private final Vector2 lastRMBClick = new Vector2(); // in UI space
    private final Vector2 lastLMBClick = new Vector2(); // in UI space
    public final Vector2 rayStart = new Vector2(); // in world space
    public final Vector2 rayEnd = new Vector2(); // in world space
    private PopupMenu contextMenu;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    @Override
    public void create() {
        super.create();
        setToEditor();

        VisUI.setDefaultTitleAlign(Align.center);

        initLeftPane();
        initMenuBar();
        initContextMenu();

        getInputMultiplexer().addProcessor(new EditorInputProcessor(this));
    }

    private void initContextMenu() {
        contextMenu = new PopupMenu();

        contextMenu.addItem(new MenuItem("Add Entity from List", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Vector3 worldSpace = getUiStage().getCamera().unproject(new Vector3(lastRMBClick.x, lastRMBClick.y, 0f));
                Vector3 unproject = getGameCamera().unproject(new Vector3(worldSpace.x, worldSpace.y, 0f));
                createNewEntity(new Vector2(unproject.x, unproject.y));
                closeContextMenu();
            }
        }));
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

    private void createNewEntity(Vector2 pos) {
        try {
            Entity entity = getSelectedItem().entity.newInstance();

            Matrix3 transform = getNewObjectTransform();
            MovementComponent mc = entity.getMovementComponent();

            mc.setScale(transform.getScale(new Vector2()));
            mc.setRotation(transform.getRotation());
            mc.setWorldPosition(transform.getTranslation(new Vector2()).add(pos));

            addEntity(entity);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        super.show();

        if (entityList == null)
            create();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Vector3 rayA = getGameCamera().project(new Vector3(rayStart.x, rayStart.y, 0f));
        Vector3 rayB = getGameCamera().project(new Vector3(rayEnd.x, rayEnd.y, 0f));

        float offset = 15f;
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.rectLine(lastLMBClick.x - offset, lastLMBClick.y, lastLMBClick.x + offset, lastLMBClick.y, 10f);
        shapeRenderer.rectLine(lastLMBClick.x, lastLMBClick.y - offset, lastLMBClick.x, lastLMBClick.y + offset, 10f);
        //shapeRenderer.rect(0, 0, 10f * getGameCamera().zoom, 10000f, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        //shapeRenderer.rect(0, 0, 10000f, 10f * getGameCamera().zoom, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
        shapeRenderer.rectLine(rayA.x, rayA.y, rayB.x, rayB.y, 2f);
        shapeRenderer.end();
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

    public Vector2 getLastRMBClick() {
        return lastRMBClick.cpy();
    }

    public void setLastRMBClick(Vector2 vec) {
        lastRMBClick.set(vec);
    }

    public Vector2 getLastLMBClick() {
        return lastLMBClick.cpy();
    }

    public void setLastLMBClick(Vector2 vec) {
        lastLMBClick.set(vec);
    }

    public void openContextMenu() {
        contextMenu.showMenu(getUiStage(), lastRMBClick.x, lastRMBClick.y);
    }

    public void closeContextMenu() {
        contextMenu.remove();
    }
}
