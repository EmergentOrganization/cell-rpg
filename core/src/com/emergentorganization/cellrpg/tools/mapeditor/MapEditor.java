package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.mapeditor.map.Map;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.emergentorganization.cellrpg.tools.mapeditor.ui.*;
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
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 8f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;
    public static float MOVE_SPEED = 20f;

    public static float BB_THICKNESS = 1f; // Bounding box thickness of lines

    private final Vector2 lastRMBClick = new Vector2(); // in UI space
    private final Vector2 lastLMBClick = new Vector2(); // in UI space
    public final Vector2 rayStart = new Vector2(); // in game space
    public final Vector2 rayEnd = new Vector2(); // in game space
    private PopupMenu contextMenu;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private MapTarget target = null;
    private VisTextField xField;
    private VisTextField yField;
    private VisTextField rotField;
    private VisTextField scaleField;

    @Override
    public void create() {
        super.create();
        setToEditor();

        VisUI.setDefaultTitleAlign(Align.center);

        initLeftPane();
        initMenuBar();
        initContextMenu();

        getInputMultiplexer().addProcessor(new EditorInputProcessor(this));

        //AxisAlignedBounds bounds = (AxisAlignedBounds) getWorld().getBounds();
        //float width = (float) bounds.getBounds().getWidth();
        //float height = (float) bounds.getBounds().getHeight();
        //worldSize = new Vector2(width, height);
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
        final VisTable table = (VisTable) menuBar.getTable();
        table.setWidth(MENU_BAR_WIDTH);
        table.setHeight(MENU_BAR_HEIGHT);
        table.setPosition(LEFT_PANEL_WIDTH, Gdx.graphics.getHeight() - MENU_BAR_HEIGHT);

        Menu menu = new Menu("File");

        MenuItem imp = new MenuItem("Import");
        menu.addItem(imp);

        MenuItem exp = new MenuItem("Export");
        menu.addItem(exp);

        menuBar.addMenu(menu);
        table.addSeparator(true);

        table.add(new VisLabel("X: "));
        xField = new VisTextField("0.0");
        table.add(xField).width(MENU_BAR_WIDTH / 8f);
        table.addSeparator(true);

        table.add(new VisLabel("Y: "));
        yField = new VisTextField("0.0");
        table.add(yField).width(MENU_BAR_WIDTH / 8f);
        table.addSeparator(true);

        table.add(new VisLabel("Rotation: "));
        rotField = new VisTextField("0.0");
        table.add(rotField).width(MENU_BAR_WIDTH / 8f);
        table.addSeparator(true);

        table.add(new VisLabel("Scale: "));
        scaleField = new VisTextField("0.0");
        table.add(scaleField).width(MENU_BAR_WIDTH / 8f);
        table.addSeparator(true);

        final MapEditor _this = this;
        imp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Import");
                Map map = MapTools.importMap("TEST_MAP_EXPORT");
                _this.addEntities(map.getEntities());
            }
        });

        exp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapTools.exportMap(_this, "TEST_MAP_EXPORT");
            }
        });

        xField.setTextFieldFilter(new TransformTextFilter());
        xField.setTextFieldListener(new VisTextField.TextFieldListener() {
            @Override
            public void keyTyped(VisTextField textField, char c) {
                try {
                    float v = Float.parseFloat(textField.getText());
                    MapTarget target = getMapTarget();
                    target.movementComponent.setWorldPosition(v, target.movementComponent.getWorldPosition().y);
                }
                catch (NumberFormatException e) {
                    // meh
                }
            }
        });
        yField.setTextFieldFilter(new TransformTextFilter());
        yField.setTextFieldListener(new VisTextField.TextFieldListener() {
            @Override
            public void keyTyped(VisTextField textField, char c) {
                try {
                    float v = Float.parseFloat(textField.getText());
                    MapTarget target = getMapTarget();
                    target.movementComponent.setWorldPosition(target.movementComponent.getWorldPosition().x, v);
                } catch (NumberFormatException e) {
                    // meh
                }
            }
        });
        rotField.setTextFieldFilter(new TransformTextFilter());
        rotField.setTextFieldListener(new VisTextField.TextFieldListener() {
            @Override
            public void keyTyped(VisTextField textField, char c) {
                try {
                    float v = Float.parseFloat(textField.getText());
                    MapTarget target = getMapTarget();
                    target.movementComponent.setRotation(v);

                    /*PhysicsComponent physComp = (PhysicsComponent) target.target.getFirstComponentByType(ComponentType.PHYSICS);
                    if (physComp != null)
                        physComp.getBody().rotate(v);*/ // TODO
                } catch (NumberFormatException e) {
                    // meh
                }
            }
        });
        scaleField.setTextFieldFilter(new TransformTextFilter());
        scaleField.setTextFieldListener(new VisTextField.TextFieldListener() {
            @Override
            public void keyTyped(VisTextField textField, char c) {
                try {
                    float v = Float.parseFloat(textField.getText());
                    MapTarget target = getMapTarget();
                    target.movementComponent.setScale(v);
                } catch (NumberFormatException e) {
                    // meh
                }
            }
        });

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

        shapeRenderer.setProjectionMatrix(getGameCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // x / y axis
        shapeRenderer.rect(0f, 0f, 10000f, 0.1f * getGameCamera().zoom, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);
        shapeRenderer.rect(0f, 0f, 0.1f * getGameCamera().zoom, 10000f, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);

        // selected object bounds
        if (target != null) {
            Vector2 size = target.size;
            Vector2 pos = target.movementComponent.getWorldPosition();
            drawBoundingBox(size, new Vector2(pos.x, pos.y));
        }

        //debug lines
        float offset = 15f;
        shapeRenderer.setProjectionMatrix(getUiStage().getCamera().combined);
        shapeRenderer.rectLine(lastLMBClick.x - offset, lastLMBClick.y, lastLMBClick.x + offset, lastLMBClick.y, 1f);
        shapeRenderer.rectLine(lastLMBClick.x, lastLMBClick.y - offset, lastLMBClick.x, lastLMBClick.y + offset, 1f);
        shapeRenderer.rectLine(rayA.x, rayA.y, rayB.x, rayB.y, 2f);
        shapeRenderer.end();

        drawUI();

        handleInput();
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

        float speed = MOVE_SPEED * getGameCamera().zoom * Gdx.graphics.getDeltaTime();
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

        if (this.target == null) {
            xField.setText("0.0");
            yField.setText("0.0");
            rotField.setText("0.0");
            scaleField.setText("0.0");
        }
        else {
            xField.setText(String.valueOf(target.movementComponent.getWorldPosition().x));
            yField.setText(String.valueOf(target.movementComponent.getWorldPosition().y));
            rotField.setText(String.valueOf(target.movementComponent.getRotation()));
            scaleField.setText(String.valueOf(target.movementComponent.getScale().x)); //TODO Fix this
        }
    }

    public MapTarget getMapTarget() { return target; }
}
