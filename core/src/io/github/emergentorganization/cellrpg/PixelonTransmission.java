package io.github.emergentorganization.cellrpg;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.kotcrab.vis.ui.VisUI;
import io.github.emergentorganization.cellrpg.components.EquipmentList;
import io.github.emergentorganization.cellrpg.components.StatsTracker;
import io.github.emergentorganization.cellrpg.core.Tags;
import io.github.emergentorganization.cellrpg.managers.RegionManager.LeveledRegionSwitcher;
import io.github.emergentorganization.cellrpg.scenes.BaseScene;
import io.github.emergentorganization.cellrpg.scenes.Scene;
import io.github.emergentorganization.cellrpg.scenes.game.regions.WarpInEventRegion;
import io.github.emergentorganization.cellrpg.tools.FileStructure;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;
import io.github.emergentorganization.cellrpg.tools.Resources;
import io.github.emergentorganization.cellrpg.tools.Scores;
import io.github.emergentorganization.cellrpg.tools.mixpanel.Mixpanel;
import io.github.emergentorganization.cellrpg.tools.mixpanel.Secrets;
import io.github.emergentorganization.cellrpg.tools.physics.BodyEditorLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class PixelonTransmission extends Game {

    public static final float PHYSICS_TIMESTEP = 1 / 45f;
    private static final String ATLAS_PATH = FileStructure.RESOURCE_DIR + "textures/TexturePack.atlas";
    private static final String COLLIDER_PATH = FileStructure.RESOURCE_DIR + "/data/colliderProject";

    static {
        GdxNativesLoader.load();
    }

    private final Logger logger;
    public Mixpanel mixpanel;
    public Scores scores;
    public int playerScore = 0;
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;
    private FileStructure fileStructure;
    private Skin skin;
    private BodyEditorLoader bodyLoader;
    private String version;

    public PixelonTransmission() {
        String logFile = "log4j2.xml";
        System.setProperty("log4j.configurationFile", FileStructure.RESOURCE_DIR + logFile);
        logger = LogManager.getLogger(getClass());
        Preferences prefs = GameSettings.getPreferences();
        String type = prefs.getString(GameSettings.KEY_GRAPHICS_TYPE, "windowed");
        int width = prefs.getInteger(GameSettings.KEY_GRAPHICS_WIDTH, 0);
        int height = prefs.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT, 0);
        logger.info("init app " + width + "x" + height + " " + type);
    }

    @Override
    public void create() {
        GLProfiler.enable();
        GLProfiler.listener = new GLErrorListener() {
            @Override
            public void onError(int error) {
                String stringError = GLProfiler.resolveErrorNumber(error);
                logger.debug("GL ERROR: " + stringError);
            }
        };

        // init file structure
        this.fileStructure = new FileStructure();
        if (fileStructure.isJar()) {
            fileStructure.unpackAssets();
        }

        VisUI.load();

        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load(ATLAS_PATH, TextureAtlas.class);
        loadSounds();
        loadMusic();
        assetManager.finishLoading();
        textureAtlas = assetManager.get(ATLAS_PATH, TextureAtlas.class);

        bodyLoader = new BodyEditorLoader(Gdx.files.internal(COLLIDER_PATH));

        skin = new Skin(
                Gdx.files.internal("resources/uiskin/skin.json"),
                new TextureAtlas(Gdx.files.internal("resources/uiskin/skin.atlas"))
        );

        scores = new Scores();
        Secrets.initialize();
        mixpanel = new Mixpanel(getVersion());
        mixpanel.initialize();

        logger.info("Game started");
        setScene(Scene.MAIN_MENU);
    }

    public void setScene(Scene sceneKey) {
        BaseScene old = (BaseScene) getScreen();
        if (old != null) {
            old.onSceneChange();
            setScreen(sceneKey.getScene(this));
            old.dispose();
        } else {
            setScreen(sceneKey.getScene(this));
        }
    }

    public BaseScene getCurrentScene() {
        return (BaseScene) getScreen();
    }

    public void gameOver(World world) {
        Entity player = world.getSystem(TagManager.class).getEntity(Tags.PLAYER);
        player.getComponent(EquipmentList.class).saveEquipment();
        playerScore = player.getComponent(StatsTracker.class).getScore();
        LeveledRegionSwitcher switcher = world.getSystem(LeveledRegionSwitcher.class);
        if (switcher != null) {  // arcade region exit to postgame menu
            WarpInEventRegion warpRegion = (WarpInEventRegion) switcher.currentRegion;
            int waveNumber = warpRegion.regionNumber;
            setScene(Scene.POSTGAME);
            mixpanel.gameOverEvent(playerScore, waveNumber);
        } else { // story or lab region exits directly to main menu (for now)
            setScene(Scene.MAIN_MENU);
        }
    }

    private String loadVersion() {
        Properties props = new Properties();
        File propsFile = Gdx.files.internal(FileStructure.RESOURCE_DIR + "property.settings").file();
        try {
            FileReader reader = new FileReader(propsFile);
            props.load(reader);
            String major = props.getProperty("majorVersion");
            String minor = props.getProperty("minorVersion");
            String patch = props.getProperty("patchVersion");
            String revision = props.getProperty("revision");
            reader.close();
            return major + "." + minor + "." + patch + "+" + revision;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void loadMusic() {
        String prefix = FileStructure.RESOURCE_DIR + "sounds/music/";
        String ext = ".ogg";
        String[] musics = {
                "always_on/pad",
                "always_on/keys"
        };

        for (String sound : musics) {
            assetManager.load(prefix + sound + ext, Sound.class);
        }

        String loopDir = Resources.DIR_SOUNDS + "music/arcade_30s_loops";
        FileHandle dirs = Gdx.files.getFileHandle(loopDir, Files.FileType.Internal);
        for (FileHandle dir : dirs.list()) {
            assetManager.load(dir.path(), Sound.class);
        }

        logger.info("music loops loading from " + loopDir);
    }

    private void loadSounds() {
        String prefix = FileStructure.RESOURCE_DIR + "sounds/";
        String ext = ".wav";
        String[] sounds = {
                "Hit",
                "PlayerHurt",
                "Explosion",
                "Select",
                "ShieldDown",
                "LaserShot",
                "ShootBlank",
                "UIBack",
                "UIBackLong",
                "UIConfirm"
        };

        for (String sound : sounds) {
            assetManager.load(prefix + sound + ext, Sound.class);
        }
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        mixpanel.dispose();
        scores.dispose();
        GameSettings.dispose();
        logger.info("Game shutdown");
    }

    public String getVersion() {
        if (version == null || version.isEmpty()) {
            version = loadVersion();
        }
        return version;
    }

    public AssetManager getGdxAssetManager() {
        return assetManager;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public Skin getUISkin() {
        return skin;
    }

    public BodyEditorLoader getBodyLoader() {
        return bodyLoader;
    }

    public FileStructure getFileStructure() {
        return fileStructure;
    }
}
