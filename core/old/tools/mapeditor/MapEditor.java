package com.emergentorganization.cellrpg.tools.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.components.entity.PhysicsComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.scenes.mainmenu.FileListNode;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.emergentorganization.cellrpg.tools.mapeditor.map.Map;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.emergentorganization.cellrpg.tools.mapeditor.ui.*;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;

import java.io.File;

/**
 * Created by BrianErikson on 6/14/2015.
 */
public class MapEditor extends Scene {
    private VisList<EntityListNode> entityList;
    private EntityListNode selectedItem;
    public String selectedMapName = "";
    
    private final Matrix3 scaler = new Matrix3().setToScaling(Scene.scale, Scene.scale);
    private final Matrix3 rotator = new Matrix3();
    private final Matrix3 translator = new Matrix3();

    public static float LEFT_PANEL_HEIGHT = Gdx.graphics.getHeight();
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 8f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;
    public static float SAVE_WINDOW_WIDTH = Gdx.graphics.getWidth() / 6f;
    public static float SAVE_WINDOW_HEIGHT = SAVE_WINDOW_WIDTH / 1.5f;
    public static float MOVE_SPEED = 20f;
    public static float MIN_ZOOM = 0.1f;
    public static float ZOOM_AMT = 0.1f; // amount of zoom per keypress

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

    private final VisWindow saveWindow = new VisWindow("Save", true);
    private final VisWindow loadWindow = new VisWindow("Load", true);
    private boolean mapInputEnabled = true;

    @Override
    public void create() {
        super.create();
        setToEditor();

        // fixes bug #29. Effectively disables NarrowphaseDetector from doing penetration testing
        getWorld().setNarrowphaseDetector(new NarrowphaseDetector() {
            @Override
            public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2,
                                  Penetration penetration) {
                return false;
            }

            @Override
            public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
                return false;
            }
        });

        VisUI.setDefaultTitleAlign(Align.center);

        initLeftPane();
        initMenuBar();
        initContextMenu();

        initSaveWindow();
        initLoadWindow();

        getInputMultiplexer().addProcessor(new EditorInputProcessor(this));
        getInputMultiplexer().addProcessor(new GestureDetector(new EditorGestureListener(getGameCamera())));
    }

    private void initSaveWindow() {
        final float PADDING = 2f;

        saveWindow.setWidth(SAVE_WINDOW_WIDTH);
        saveWindow.setHeight(SAVE_WINDOW_HEIGHT);
        saveWindow.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);

        VisTable table = new VisTable();

        VisLabel fileNameLabel = new VisLabel("Map Name", Align.center);
        table.add(fileNameLabel).pad(PADDING).fill(true, false).colspan(2).row();

        final VisTextField fileField = new VisTextField();
        table.add(fileField).pad(PADDING).fill(true, false).colspan(2).row();

        VisTextButton save = new VisTextButton("Save");
        table.add(save).pad(PADDING).fill(true, false);

        VisTextButton cancel = new VisTextButton("Cancel");
        table.add(cancel).pad(PADDING).fill(true, false);

        saveWindow.add(table).expand().fill();
        saveWindow.getTitleLabel().setColor(Color.GRAY);

        final MapEditor _this = this;
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if (!fileField.getText().isEmpty()) {
                    MapTools.exportMap(_this, fileField.getText());
                    setSaveWindowVisible(false);
                }
            }
        });

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                setSaveWindowVisible(false);
            }
        });
    }

    private void initLoadWindow() {
        final float PADDING = 2f;

        loadWindow.setWidth(SAVE_WINDOW_WIDTH);
        loadWindow.setHeight(SAVE_WINDOW_HEIGHT);
        loadWindow.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);

        final VisList<FileListNode> list = new VisList<FileListNode>();

        FileListNode[] maps = getMaps();
        if (getMaps() != null) {
            list.setItems(getMaps());
            selectedMapName = list.getSelected().file.getName();
        }

        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedMapName = list.getSelected().file.getName();
            }
        });

        VisScrollPane scrollPane = new VisScrollPane(list);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setupFadeScrollBars(1f, 0.3f);
        scrollPane.setupOverscroll(20f, 30f, 200f);

        VisTable table = new VisTable();

        VisTextButton load = new VisTextButton("Load");
        table.add(load).pad(PADDING).expand().fill().align(Align.left);

        VisTextButton cancel = new VisTextButton("Cancel");
        table.add(cancel).pad(PADDING).fill();

        loadWindow.add(scrollPane).pad(PADDING).expand().fill(true, false).row();
        loadWindow.add(table).expand().fill(true, false);

        final MapEditor _this = this;
        load.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String fileName = list.getSelected().file.getName();
                String mapName = fileName.substring(0, fileName.length() - MapTools.EXTENSION.length());

                Map map = MapTools.importMap(mapName);
                clearMap();
                _this.addEntities(map.getEntities());
                setLoadWindowVisible(false);
            }
        });

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setLoadWindowVisible(false);
            }
        });
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

        MenuItem clear = new MenuItem("Clear Map");
        menu.addItem(clear);

        MenuItem imp = new MenuItem("Import");
        menu.addItem(imp);

        MenuItem exp = new MenuItem("Export");
        menu.addItem(exp);

        MenuItem exit = new MenuItem("Exit to Main Menu");
        menu.addItem(exit);

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
        clear.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clearMap();
            }
        });

        imp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setLoadWindowVisible(true);
            }
        });

        exp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setSaveWindowVisible(true);
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: Throw warning window saying that changes won't be saved if they continue
                CellRpg.fetch().setScreen(new MainMenu("done editing?"));
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

                    /*PhysicsComponent physComp = (PhysicsComponent) target.target.getFirstComponentByType(PhysicsComponent.class);
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

    private FileListNode[] getMaps() {
        File folder = new File(MapTools.FOLDER_ROOT);

        if (folder.exists()) {
            File[] files = folder.listFiles();
            FileListNode[] fileNodes = new FileListNode[files.length];
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.getName().contains(MapTools.EXTENSION)) {
                    fileNodes[i] = new FileListNode(file);
                }
            }

            return fileNodes;
        }

        return null;
    }

    private void createNewEntity(Vector2 pos) {
        try {
            Entity entity = getSelectedItem().entity.newInstance();

            Matrix3 transform = getNewObjectTransform();
            MovementComponent mc = entity.getFirstComponentByType(MovementComponent.class);

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
            AABB rect = (target.target.getFirstComponentByType(PhysicsComponent.class)).getBody().createAABB();
            Vector2 size = new Vector2((float)rect.getWidth(), (float)rect.getHeight());
            Vector2 pos = target.movementComponent.getWorldPosition();
            drawBoundingBox(size, new Vector2(pos.x, pos.y));
        }

        shapeRenderer.end();

        drawUI();

        handleInput();
    }

    private void clearMap() {
        getEntities().clear();
        getWorld().removeAllBodies();
        setMapTarget(null);
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
        if (mapInputEnabled) {
            boolean update = false;

            OrthographicCamera camera = getGameCamera();
            float speed = MOVE_SPEED * camera.zoom * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                update = true;
                camera.position.add(0f, speed, 0f);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                update = true;
                camera.position.add(0f, -speed, 0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                update = true;
                camera.position.add(speed, 0f, 0f);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                update = true;
                camera.position.add(-speed, 0f, 0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Z)) { // zoom in
                update = true;
                camera.zoom -= ZOOM_AMT;
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.X)) { // zoom out
                update = true;
                camera.zoom += ZOOM_AMT;
            }

            if (update) getGameCamera().update();

            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) && target != null) {
                removeEntity(target.target);
                PhysicsComponent phys = target.target.getFirstComponentByType(PhysicsComponent.class);
                if (phys != null)
                    getWorld().removeBody(phys.getBody());
                setMapTarget(null);
            }
        }
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
        updateTargetTransform();
    }

    public void updateTargetTransform() {
        if (this.target != null) {
            xField.setText(String.valueOf(target.movementComponent.getWorldPosition().x));
            yField.setText(String.valueOf(target.movementComponent.getWorldPosition().y));
            rotField.setText(String.valueOf(target.movementComponent.getRotation()));
            scaleField.setText(String.valueOf(target.movementComponent.getScale().x)); //TODO Fix this
        }
        else {
            xField.setText("0.0");
            yField.setText("0.0");
            rotField.setText("0.0");
            scaleField.setText("0.0");
        }
    }

    public MapTarget getMapTarget() { return target; }

    public void enableMapInput(boolean enable) {
        this.mapInputEnabled = enable;
    }

    public boolean isMapInputEnabled() {
        return mapInputEnabled;
    }

    public void setSaveWindowVisible(boolean show) {
        if (show) {
            getUiStage().addActor(saveWindow);
            saveWindow.fadeIn();
        }
        else
            saveWindow.fadeOut();

        enableMapInput(!show);
    }

    public void setLoadWindowVisible(boolean show) {
        if (show) {
            getUiStage().addActor(loadWindow);
            loadWindow.fadeIn();
        }
        else
            loadWindow.fadeOut();

        enableMapInput(!show);
    }
}
