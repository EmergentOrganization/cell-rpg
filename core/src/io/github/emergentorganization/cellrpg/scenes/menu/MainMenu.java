package io.github.emergentorganization.cellrpg.scenes.menu;

import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.core.WorldType;
import io.github.emergentorganization.cellrpg.systems.SpawningSystem;
import io.github.emergentorganization.cellrpg.core.systems.WindowSystem;
import io.github.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.scenes.game.worldscene.WorldScene;
import io.github.emergentorganization.cellrpg.scenes.game.regions.WarpInEventRegion;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainMenu extends WorldScene {
    private final Logger logger = LogManager.getLogger(getClass());

    private final float tableMargin;

    public MainMenu(PixelonTransmission pt) {
        super(pt, WorldType.STANDARD);

        this.tableMargin = stage.getWidth() * 0.015f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }

    @Override
    public void show() {
        logger.info("showing main menu");
        super.show();
        initUI();
    }

    private void initUI() {
        Skin s = pt.getUISkin();

        Table table = new Table(s);
        table.row();

        // title
        {
            Label title = new Label("Pixelon Transmission", s, "header");
            table.add(title).left().row();
        }

        // arcade
        {
            TextButton arcade = new TextButton("> Condemned Plainverse (Arcade Mode)", s);
            arcade.align(Align.left);
            arcade.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.setScene(Scene.ARCADE);
                }
            });

            table.add(arcade).left().row();
        }

        // story
        {
            TextButton story = new TextButton("> Stable Plainverse (Story Mode)", s);
            story.align(Align.left);
            story.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.setScene(Scene.STORY);
                }
            });

            table.add(story).left().row();
        }

        // lab
        {
            TextButton lab = new TextButton("> LifeGene Lab", s);
            lab.align(Align.left);
            lab.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    //pt.getSceneManager().setScene(Scene.LAB);
                }
            });

            table.add(lab).left().row();
        }

        // Map Editor
        {
            TextButton editor = new TextButton("> Map Editor", s);
            editor.align(Align.left);
            editor.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pt.setScene(Scene.EDITOR);
                }
            });

            table.add(editor).left().row();
        }

        // settings
        {
            TextButton settings = new TextButton("> Settings", s);
            settings.align(Align.left);
            settings.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    WindowSystem winSys = world.getSystem(WindowSystem.class);
                    if (winSys.isPaused()) {
                        winSys.onResume();  // exit menu if already open
                    } else {
                        winSys.onPause();  // pause when clicked
                    }
                }
            });
            table.add(settings).left().row();
        }

        // credits
        {
            TextButton credits = new TextButton("> Credits", s);
            credits.align(Align.left);
            credits.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.net.openURI("https://github.com/EmergentOrganization/cell-rpg/wiki/Credits-and-Attributions");
                }
            });
            table.add(credits).left().row();
        }

        // quit
        {
            TextButton quit = new TextButton("> Quit", s);
            quit.align(Align.left);
            quit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });

            table.add(quit).left();
        }

        // pack and position table
        table.pack();
        table.setPosition(tableMargin, tableMargin);
        stage.addActor(table);

        // version label
        {
            Label versionInfo = new Label("v" + pt.getVersion(), s);
            versionInfo.pack();
            versionInfo.setPosition((stage.getWidth() - versionInfo.getWidth()) - tableMargin, tableMargin);

            stage.addActor(versionInfo);
        }

        if (GameSettings.getPreferences().getBoolean(GameSettings.KEY_FIRST_START, true)) {
            logger.trace("starting firstStart window");
            new FirstStartWindow(stage, world, pt);
        } else {
            logger.trace("not first start");
        }
    }

    private WorldConfiguration getBaseWorldConfiguration() {
        // TODO: set up main menu visuals

        WorldConfiguration wc = new WorldConfiguration();
        // for using WarpInEventRegions:
        int maxTimeInRegion = 3 * 60 * 1000;  // max time before region moves ahead anyway [ms]
        wc.setSystem(new LeveledRegionSwitcher(maxTimeInRegion, 0));  // -1 to use test region, 0 is typical starting wave
//        // for using SingleShapeWarp and SingleEntityWarp Regions:
//        wc.setSystem(new LeveledRegionSwitcher(new SingleShapeWarpRegion(
//                this, 10*1000, CGoLShapeConsts.BLINKER_H, .5f, CALayer.vyroid_values()
//        )));

        return new WorldConfiguration();
    }

}
