package com.emergentorganization.cellrpg.tools.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class MapImage extends MapObject {
    public final MapImageType type;
    private Vector2 position;
    private Texture texture;

    public MapImage(Vector2 position, String path) {
        this.position = position;
        this.texture = new Texture(Gdx.files.internal(path));
        type = getType(path);
    }

    private static MapImageType getType(String path) {
        if (path.contains("the_edge"))
            return MapImageType.THE_EDGE;
        else if (path.contains("building-large-1"))
            return MapImageType.BUILDING_LARGE_1;
        else if (path.contains("building-round-1"))
            return MapImageType.BUILDING_ROUND_1;
        else if (path.contains("char-civ1-blinker"))
            return MapImageType.CHAR_CIV1_BLINKER;
        else if (path.contains("char-player"))
            return MapImageType.CHAR_PLAYER;
        else
            throw new RuntimeException("ERROR: Could not find MapImageType for path: " + path);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }
}
