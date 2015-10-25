package com.emergentorganization.cellrpg.scenes.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.scenes.*;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;
import com.emergentorganization.cellrpg.tools.mapeditor.map.MapTools;
import com.kotcrab.vis.ui.widget.*;

import java.io.File;

/**
 * Created by BrianErikson on 6/20/2015.
 */
public class MainMenu extends Scene {
    public String selectedMapName = "";
    private VisWindow loadWindow;

    public MainMenu(String subtitleText){
        create(subtitleText);
    }

    public void create(String subtitleText) {
        super.create();

        initMainMenu(subtitleText);
        initLoadWindow();
    }

    @Override
    public void create() {
        super.create();
        initMainMenu("...");
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

    private void initLoadWindow() {
        final VisList<FileListNode> list = new VisList<FileListNode>();

        FileListNode[] maps = getMaps();
        if (maps != null) {
            list.setItems(maps);
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
        final float PADDING = 2f;

        VisTextButton load = new VisTextButton("Initiate Bridge Connection");
        table.add(load).pad(PADDING).expand().fill().align(Align.left);

        VisTextButton cancel = new VisTextButton("Cancel");
        table.add(cancel).pad(PADDING).fill();

        loadWindow = new VisWindow("Load Map", true);
        loadWindow.setVisible(false);
        loadWindow.add(scrollPane).pad(PADDING).expand().fill(true, false).row();
        loadWindow.add(table).expand().fill(true, false);
        loadWindow.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
        getUiStage().addActor(loadWindow);

        load.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                String fileName = list.getSelected().file.getName();
                String mapName = fileName.substring(0, fileName.length() - MapTools.EXTENSION.length());
                CellRpg.fetch().setScreen(new CustomMap(mapName));
            }
        });

        final VisWindow ref = loadWindow;
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ref.setVisible(false);
            }
        });
    }

    public void setLoadWindowVisible(boolean visible) {
        loadWindow.setVisible(visible);
        loadWindow.toFront();
        System.out.println("Set window visible");
    }

    private void initMainMenu(String subtitleText) {
        final MainMenu _this = this;

        VisTable table = new VisTable();
        VisWindow window = new VisWindow("Planiverse Bridge v" +CellRpg.fetch().getVersion(), false);
        window.setFillParent(true);
        window.add(table);
        window.clearListeners();

        //VisLabel title = new VisLabel("Planiverse Bridge");
        //table.add(title).pad(0f, 0f, 10f, 0f).fill(true, false).row();

        VisLabel subtitle = new VisLabel(subtitleText);
        table.add(subtitle).pad(0f, 0f, 5f, 0f).fill(true, false).row();

        VisTextButton arcadeMode = new VisTextButton("Bridge to Condemned Planiverse (arcade mode)");
        table.add(arcadeMode).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        arcadeMode.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                CellRpg cellRpg = CellRpg.fetch();
                cellRpg.getMixpanel().newGameEvent();
                cellRpg.setScreen(new ArcadeScene());
            }
        });

        if (CellRpg.fetch().getConfiguration().isDevModeEnabled()) {
            VisTextButton storyMode = new VisTextButton("Bridge to Stable Planiverse (story mode)");
            table.add(storyMode).pad(0f, 0f, 5f, 0f).fill(true, false).row();
            storyMode.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    CellRpg cellRpg = CellRpg.fetch();
                    cellRpg.getMixpanel().newGameEvent();
                    cellRpg.setScreen(new RPGScene());
                }
            });

            VisTextButton geneticMode = new VisTextButton("Bridge to LifeGene Lab (experimental)");
            table.add(geneticMode).pad(0f, 0f, 5f, 0f).fill(true, false).row();
            geneticMode.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    CellRpg.fetch().setScreen(new GeneticArcade());
                }
            });
        }

        VisTextButton custom = new VisTextButton("Bridge to Custom Map");
        table.add(custom).pad(0f, 0f, 5f, 0f).fill(true, false).row();
        custom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _this.setLoadWindowVisible(true);
            }
        });

        VisTextButton editor = new VisTextButton("Map Editor");
        table.add(editor).fill(true, false);
        editor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                CellRpg.fetch().setScreen(new MapEditor());
            }
        });

        table.align(Align.center);

        getUiStage().addActor(window);

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        drawUI();
    }

    @Override
    public void hide() {
        super.hide();
    }
}
