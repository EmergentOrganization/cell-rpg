package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;


public class TexPacker {
    public static String TEXTURE_DIR = "./android/assets/resources/textures";

    public static void main(String[] arg) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 4096;
        settings.maxHeight = 4096;
        TexturePacker.process(settings, TEXTURE_DIR + "/unpacked", TEXTURE_DIR, "TexturePack");
    }
}
