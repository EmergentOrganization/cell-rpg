package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    public static float MOVE_SPEED = 2f;

    public static float BB_THICKNESS = 1f; // Bounding box thickness of lines

    private final Vector2 lastRMBClick = new Vector2(); // in UI space
    private final Vector2 lastLMBClick = new Vector2(); // in UI space
    public final Vector2 rayStart = new Vector2(); // in world space
    public final Vector2 rayEnd = new Vector2(); // in world space
    private PopupMenu contextMenu;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private MapTarget target = null;

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

        handleInput();

        Vector3 rayA = getGameCamera().project(new Vector3(rayStart.x, rayStart.y, 0f));
        Vector3 rayB = getGameCamera().project(new Vector3(rayEnd.x, rayEnd.y, 0f));

        float offset = 15f;
        shapeRenderer.setProjectionMatrix(getGameCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (target != null) {
            Vector2 size = target.size;
            Vector2 pos = target.movementComponent.getWorldPosition();
            drawBoundingBox(size, new Vector2(pos.x, pos.y));
        }
        shapeRenderer.setProjectionMatrix(getUiStage().getCamera().combined);
        shapeRenderer.rectLine(lastLMBClick.x - offset, lastLMBClick.y, lastLMBClick.x + offset, lastLMBClick.y, 1f);
        shapeRenderer.rectLine(lastLMBClick.x, lastLMBClick.y - offset, lastLMBClick.x, lastLMBClick.y + offset, 1f);
        shapeRenderer.rectLine(rayA.x, rayA.y, rayB.x, rayB.y, 2f);
        shapeRenderer.end();
    }

    /**
     * Must call between ShapeRenderer.begin() and ShapeRenderer.end()
     * @param size Scaled size of the object
     * @param pos center origin of object
     */
    private void drawBoundingBox(Vector2 size, Vector2 pos) {
        Vector2 hs = size.cpy().scl(0.5f);
        shapeRenderer.rectLine(pos.x - hs.x, pos.y - hs.y, pos.x + hs.x, pos.y - hs.y, BB_THICKNESS); // bl to br
        shapeRenderer.rectLine(pos.x + hs.x, pos.y - hs.y, pos.x + hs.x, pos.y + hs.y, BB_THICKNESS); // br to tr
        shapeRenderer.rectLine(pos.x + hs.x, pos.y + hs.y, pos.x - hs.x, pos.y + hs.y, BB_THICKNESS); // tr to tl
        shapeRenderer.rectLine(pos.x - hs.x, pos.y + hs.y, pos.x - hs.x, pos.y - hs.y, BB_THICKNESS); // tl to bl
    }

    private void handleInput() {
        boolean update = false;

        float speed = MOVE_SPEED * getGameCamera().zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            update = true;
            getGameCamera().position.add(0f, speed, 0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            update = true;
            getGameCamera().position.add(0f, -speed, 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            update = true;
            getGameCamera().position.add(speed, 0f, 0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            update = true;
            getGameCamera().position.add(-speed, 0f, 0f);
        }

        if (update) getGameCamera().update();
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

    public void setMapTarget(MapTarget target) {
        this.target = target;
    }

    public MapTarget getMapTarget() { return target; }
}
