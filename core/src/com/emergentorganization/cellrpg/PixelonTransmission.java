package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.emergentorganization.cellrpg.scenes.ArtemisScene;
import com.emergentorganization.cellrpg.tools.FileStructure;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by brian on 10/28/15.
 */
public class PixelonTransmission extends Game {
    private static class PixelonTransmissionHolder {private static final PixelonTransmission INSTANCE = new PixelonTransmission();}
    public static PixelonTransmission fetch() {
        return PixelonTransmissionHolder.INSTANCE;
    }

    private String version;
    private static final String ATLAS_PATH = FileStructure.RESOURCE_DIR + "textures/TexturePack.atlas";

    private final Logger logger;
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public PixelonTransmission() {
        System.setProperty("log4j.configurationFile", FileStructure.RESOURCE_DIR + "log4j2.xml");
        logger = LogManager.getLogger(getClass());
    }

    @Override
    public void create() {
        FileStructure.fetch().initialize();
        version = loadVersion();
        VisUI.load();

        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load(ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();
        textureAtlas = assetManager.get(ATLAS_PATH, TextureAtlas.class);

        setScreen(new ArtemisScene());
    }

    public String loadVersion() {
        Properties props = new Properties();
        File propsFile = Gdx.files.internal(FileStructure.RESOURCE_DIR + "property.settings").file();
        try {
            FileReader reader = new FileReader(propsFile);
            props.load(reader);
            String major = props.getProperty("majorVersion");
            String minor = props.getProperty("minorVersion");
            String revision = props.getProperty("revision");
            reader.close();
            return major + "." + minor + "." + revision;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
}
