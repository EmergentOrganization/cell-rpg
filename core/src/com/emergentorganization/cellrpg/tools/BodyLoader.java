package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by BrianErikson on 6/5/2015.
 */
public class BodyLoader extends BodyEditorLoader {
    public BodyLoader(String str) {
        super(str);
    }

    public BodyLoader(FileHandle file) {
        super(file);
    }

    public void getMesh() {
        // TODO: Generate com.badlogic.gdx.math.Shape2D shapes and somehow include it in a collision engine
    }
}
