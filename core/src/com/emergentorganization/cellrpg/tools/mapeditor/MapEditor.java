package com.emergentorganization.cellrpg.tools.mapeditor;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.components.*;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.core.EntityIDs;
import com.emergentorganization.cellrpg.core.SceneFactory;
import com.emergentorganization.cellrpg.managers.BodyManager;
import com.emergentorganization.cellrpg.scenes.BaseScene;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import com.emergentorganization.cellrpg.systems.InputSystem;
import com.emergentorganization.cellrpg.systems.RenderSystem;
import com.emergentorganization.cellrpg.tools.FileListNode;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.emergentorganization.cellrpg.tools.mapeditor.ui.*;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import java.io.File;

/**
 * Created by BrianErikson on 6/14/2015.
 */
public class MapEditor extends BaseScene {
    private final com.badlogic.gdx.physics.box2d.World physWorld;
    private final OrthographicCamera gameCamera;
    private final InputMultiplexer multiplexer;
    private String selectedItem;

    public static float LEFT_PANEL_HEIGHT = Gdx.graphics.getHeight();
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 8f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;
    public static float SAVE_WINDOW_WIDTH = Gdx.graphics.getWidth() / 6f;
    public static float SAVE_WINDOW_HEIGHT = SAVE_WINDOW_WIDTH / 1.5f;
    public static float MOVE_SPEED = 300f;
    public static float MIN_ZOOM = 0.001f;
    public static float ZOOM_AMT = 0.001f; // amount of zoom per keypress

    private static final float AXIS_POLE_SIZE = 1.0f; // size of the axis poles denoting 0,0
    public static float BB_THICKNESS = 0.05f; // Bounding box thickness of lines

    private final Vector2 lastRMBClick = new Vector2(); // in UI space
    private final Vector2 lastLMBClick = new Vector2(); // in UI space
    private PopupMenu contextMenu;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private Entity target = null;
    private VisTextField xField;
    private VisTextField yField;
    private VisTextField rotField;
    private VisTextField scaleField;

    private final VisWindow saveWindow = new VisWindow("Save", true);
    private final VisWindow loadWindow = new VisWindow("Load", true);
    private boolean mapInputEnabled = true;
    private SpriteBatch batch;
    private World world;
    private EntityFactory entityFactory;
    private BodyManager bodyManager;
    private VisList<FileListNode> importList;

    public MapEditor(PixelonTransmission pt) {
        super(pt);

        physWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(), true);
        initArtemis(physWorld);

        VisUI.setDefaultTitleAlign(Align.center);

        initLeftPane();
        initMenuBar();
        initContextMenu();

        initSaveWindow();
        initLoadWindow();

        gameCamera = (OrthographicCamera) world.getSystem(CameraSystem.class).getGameCamera();

        multiplexer = new InputMultiplexer(stage, new EditorInputProcessor(this));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initArtemis(com.badlogic.gdx.physics.box2d.World physWorld) {
        batch = new SpriteBatch();
        entityFactory = new EntityFactory();
        world = new World(SceneFactory.basicGameConfiguration(pt, physWorld, batch, stage, entityFactory));
        entityFactory.initialize(world);
        bodyManager = world.getSystem(BodyManager.class);
        entityFactory.createPlayer(0, 0);
        world.getSystem(InputSystem.class).setEnabled(false);
        world.getSystem(CameraSystem.class).setCamFollow(false);
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
                    MapTools.exportMap(world, fileField.getText());
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

        importList = new VisList<FileListNode>();

        FileListNode[] maps = getMaps();
        if (maps != null) {
            importList.setItems(getMaps());
        }

        final VisScrollPane scrollPane = new VisScrollPane(importList);
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
                FileListNode selected = importList.getSelected();
                if (selected != null) {
                    String fileName = selected.file.getName();
                    String mapName = fileName.substring(0, fileName.length() - MapTools.EXTENSION.length());

                    clearMap();
                    MapTools.importMap(mapName, entityFactory);
                    setLoadWindowVisible(false);
                }
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
                Vector3 worldSpace = stage.getCamera().unproject(new Vector3(lastRMBClick.x, lastRMBClick.y, 0f));
                Vector3 unproject = gameCamera.unproject(new Vector3(worldSpace.x, worldSpace.y, 0f));
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
                pt.getSceneManager().setScene(Scene.MAIN_MENU);
            }
        });

        xField.setTextFieldFilter(new TransformTextFilter());
        xField.setTextFieldListener(new VisTextField.TextFieldListener() {
            @Override
            public void keyTyped(VisTextField textField, char c) {
                try {
                    float v = Float.parseFloat(textField.getText());
                    if (target != null) {
                        Body body = bodyManager.getBody(target.getId());
                        if (body != null)
                            body.setTransform(v, body.getPosition().y, body.getAngle());
                        else
                            target.getComponent(Position.class).position.x = v;
                    }
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
                    if (target != null) {
                        Body body = bodyManager.getBody(target.getId());
                        if (body != null)
                            body.setTransform(body.getPosition().x, v, body.getAngle());
                        else
                            target.getComponent(Position.class).position.y = v;
                    }
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
                    if (target != null) {
                        Body body = bodyManager.getBody(target.getId());
                        if (body != null)
                            body.setTransform(body.getPosition().x, body.getPosition().y, MathUtils.degreesToRadians * v);
                        else
                            target.getComponent(Rotation.class).angle = v;
                    }
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
                    if (target != null)
                        target.getComponent(Scale.class).scale = v; // TODO: rebuild colliders on resize
                } catch (NumberFormatException e) {
                    // meh
                }
            }
        });

        stage.addActor(menuBar.getTable());
    }

    private void initLeftPane() {
        VisList<String> entityList = new VisList<String>();
        entityList.setVisible(true);

        entityList.setItems(EntityIDs.getIDs());
        selectedItem = entityList.getItems().get(entityList.getSelectedIndex());
        final VisList<String> listRef = entityList;
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
        stage.addActor(leftPane);
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
        entityFactory.createEntityByID(selectedItem, pos, 0f);
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.setDelta(delta);
        world.process();

        physWorld.step(PixelonTransmission.PHYSICS_TIMESTEP, 6, 2);

        shapeRenderer.setProjectionMatrix(gameCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // x / y axis
        shapeRenderer.rect(0f, 0f, 10000f, AXIS_POLE_SIZE * gameCamera.zoom, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);
        shapeRenderer.rect(0f, 0f, AXIS_POLE_SIZE * gameCamera.zoom, 10000f, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY);

        // selected object bounds
        if (target != null) {
            Bounds bounds = target.getComponent(Bounds.class);
            Vector2 size = new Vector2(bounds.width, bounds.height); // stretch/shrink bounding box with rotation
            Vector2 pos = target.getComponent(Position.class).position.cpy().add(size.cpy().scl(0.5f));
            drawBoundingBox(size, new Vector2(pos.x, pos.y));
        }

        shapeRenderer.end();

        super.render(delta);
    }

    private void clearMap() {
        for (Integer id : world.getSystem(RenderSystem.class).getSortedEntityIds()) {
            world.delete(id);
        }

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

            float speed = MOVE_SPEED * gameCamera.zoom * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                update = true;
                gameCamera.position.add(0f, speed, 0f);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                update = true;
                gameCamera.position.add(0f, -speed, 0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                update = true;
                gameCamera.position.add(speed, 0f, 0f);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                update = true;
                gameCamera.position.add(-speed, 0f, 0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Z)) { // zoom in
                update = true;
                gameCamera.zoom -= ZOOM_AMT;
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.X)) { // zoom out
                update = true;
                gameCamera.zoom += ZOOM_AMT;
            }

            if (update) gameCamera.update();

            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) && target != null) {
                target.deleteFromWorld();
                setMapTarget(null);
            }
        }
    }

    @Override
    public void hide() {

    }

    public String getSelectedItem() {
        return selectedItem;
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
        contextMenu.showMenu(stage, lastRMBClick.x, lastRMBClick.y);
    }

    public void closeContextMenu() {
        contextMenu.remove();
    }

    public void setMapTarget(Entity target) {
        this.target = target;
        updateTargetTransform();
    }

    public void updateTargetTransform() {
        if (this.target != null) {
            Vector2 pos = target.getComponent(Position.class).position;
            float rot = target.getComponent(Rotation.class).angle;
            float scale = target.getComponent(Scale.class).scale;
            xField.setText(String.valueOf(pos.x));
            yField.setText(String.valueOf(pos.y));
            rotField.setText(String.valueOf(rot));
            scaleField.setText(String.valueOf(scale));
        }
        else {
            xField.setText("0.0");
            yField.setText("0.0");
            rotField.setText("0.0");
            scaleField.setText("0.0");
        }
    }

    public Entity getMapTarget() { return target; }

    public void enableMapInput(boolean enable) {
        this.mapInputEnabled = enable;
    }

    public boolean isMapInputEnabled() {
        return mapInputEnabled;
    }

    public void setSaveWindowVisible(boolean show) {
        if (show) {
            stage.addActor(saveWindow);
            saveWindow.fadeIn();
        }
        else
            saveWindow.fadeOut();

        enableMapInput(!show);
    }

    public void setLoadWindowVisible(boolean show) {
        if (show) {
            stage.addActor(loadWindow);
            refreshMapList();
            loadWindow.fadeIn();
        }
        else
            loadWindow.fadeOut();

        enableMapInput(!show);
    }

    private void refreshMapList() {
        FileListNode[] maps = getMaps();
        if (maps != null)
            importList.setItems(maps);
    }

    public Stage getUiStage() {
        return stage;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public World getWorld() {
        return world;
    }

    public com.badlogic.gdx.physics.box2d.World getPhysWorld() {
        return physWorld;
    }
}
