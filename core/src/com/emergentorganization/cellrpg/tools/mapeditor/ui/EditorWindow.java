package com.emergentorganization.cellrpg.tools.mapeditor.ui;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.PixelonTransmission;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.components.Rotation;
import com.emergentorganization.cellrpg.components.Scale;
import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.managers.PhysicsSystem;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.tools.FileListNode;
import com.emergentorganization.cellrpg.tools.mapeditor.EditorTarget;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/**
 * Created by brian on 12/12/15.
 */
public class EditorWindow {
    public static float LEFT_PANEL_HEIGHT = Gdx.graphics.getHeight();
    public static float LEFT_PANEL_WIDTH = Gdx.graphics.getWidth() / 8f;
    public static float MENU_BAR_HEIGHT = Gdx.graphics.getHeight() / 19f;
    public static float MENU_BAR_WIDTH = Gdx.graphics.getWidth() - LEFT_PANEL_WIDTH;
    public static float SAVE_WINDOW_WIDTH = Gdx.graphics.getWidth() / 6f;
    public static float SAVE_WINDOW_HEIGHT = SAVE_WINDOW_WIDTH / 1.5f;
    private final Stage stage;
    private final MapEditor editor;
    private final PixelonTransmission pt;

    private VisTextField xField;
    private VisTextField yField;
    private VisTextField rotField;
    private VisTextField scaleField;
    private final VisWindow saveWindow = new VisWindow("Save", true);
    private final VisWindow loadWindow = new VisWindow("Load", true);
    private PopupMenu contextMenu;
    private VisList<FileListNode> importList;
    private String selectedItem;

    public EditorWindow(PixelonTransmission pt, MapEditor editor, Stage stage, World world) {
        this.pt = pt;
        this.editor = editor;
        this.stage = stage;

        VisUI.setDefaultTitleAlign(Align.center);

        initLeftPane();
        initMenuBar(world);
        initContextMenu();

        initSaveWindow(world);
        initLoadWindow();
    }

    private void initSaveWindow(final World world) {
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

        FileListNode[] maps = editor.getMaps();
        if (maps != null) {
            importList.setItems(maps);
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

        load.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                FileListNode selected = importList.getSelected();
                if (selected != null) {
                    String fileName = selected.file.getName();
                    String mapName = fileName.substring(0, fileName.length() - MapTools.EXTENSION.length());

                    editor.load(mapName);
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
                editor.createNewEntity(EntityID.fromString(selectedItem), editor.getLastLMBClick());
                closeContextMenu();
            }
        }));
    }

    private void initMenuBar(final World world) {
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
                editor.clearMap();
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
        final PhysicsSystem physicsSystem = world.getSystem(PhysicsSystem.class);
        xField.setTextFieldFilter(new TransformTextFilter());
        xField.setTextFieldListener(new VisTextField.TextFieldListener() {
            @Override
            public void keyTyped(VisTextField textField, char c) {
                try {
                    float v = Float.parseFloat(textField.getText());
                    EditorTarget target = editor.getTarget();
                    if (target != null) {
                        Body body = physicsSystem.getBody(target.getEntity().getId());
                        if (body != null)
                            body.setTransform(v, body.getPosition().y, body.getAngle());
                        else
                            target.getEntity().getComponent(Position.class).position.x = v;
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
                    EditorTarget target = editor.getTarget();
                    if (target != null) {
                        Body body = physicsSystem.getBody(target.getEntity().getId());
                        if (body != null)
                            body.setTransform(body.getPosition().x, v, body.getAngle());
                        else
                            target.getEntity().getComponent(Position.class).position.y = v;
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
                    EditorTarget target = editor.getTarget();
                    if (target != null) {
                        Body body = physicsSystem.getBody(target.getEntity().getId());
                        if (body != null)
                            body.setTransform(body.getPosition().x, body.getPosition().y, MathUtils.degreesToRadians * v);
                        else
                            target.getEntity().getComponent(Rotation.class).angle = v;
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
                    EditorTarget target = editor.getTarget();
                    if (target != null)
                        target.getEntity().getComponent(Scale.class).scale = v; // TODO: rebuild colliders on resize
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

        entityList.setItems(EntityID.getIDs());
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

    public void openContextMenu(Vector2 pos) {
        contextMenu.showMenu(stage, pos.x, pos.y);
    }

    public void closeContextMenu() {
        contextMenu.remove();
    }

    public void setSaveWindowVisible(boolean show) {
        if (show) {
            stage.addActor(saveWindow);
            saveWindow.fadeIn();
        }
        else
            saveWindow.fadeOut();

        editor.setMapInput(!show);
    }

    public void setLoadWindowVisible(boolean show) {
        if (show) {
            stage.addActor(loadWindow);
            importList.setItems(editor.getMaps());
            loadWindow.fadeIn();
        }
        else
            loadWindow.fadeOut();

        editor.setMapInput(!show);
    }

    public void updateTransform(Entity entity) {
        Vector2 pos = entity.getComponent(Position.class).position;
        float rot = entity.getComponent(Rotation.class).angle;
        float scale = entity.getComponent(Scale.class).scale;
        xField.setText(String.valueOf(pos.x));
        yField.setText(String.valueOf(pos.y));
        rotField.setText(String.valueOf(rot));
        scaleField.setText(String.valueOf(scale));
    }

    public void zeroizeTransform() {
        xField.setText("0.0");
        yField.setText("0.0");
        rotField.setText("0.0");
        scaleField.setText("0.0");
    }
}
