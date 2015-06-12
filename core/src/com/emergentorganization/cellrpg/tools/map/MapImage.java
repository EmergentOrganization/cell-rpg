package com.emergentorganization.cellrpg.tools.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class MapImage extends MapObject {
    private Vector2 position;
    private Texture texture;

    public MapImage(Vector2 position, String path) {
        this.position = position;
        this.texture = new Texture(Gdx.files.internal(path));
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }
}
