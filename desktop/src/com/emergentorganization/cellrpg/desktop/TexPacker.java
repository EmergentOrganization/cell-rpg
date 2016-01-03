package com.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by BrianErikson on 9/17/15.
 */
public class TexPacker {
    public static String TEXTURE_DIR = "./android/assets/resources/textures";

    public static void main(String[] arg) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 4096;
        settings.maxHeight = 4096;
        TexturePacker.process(settings, TEXTURE_DIR + "/unpacked", TEXTURE_DIR, "TexturePack");
    }
}
