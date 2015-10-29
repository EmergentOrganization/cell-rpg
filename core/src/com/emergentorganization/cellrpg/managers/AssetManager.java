package com.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Created by orelb on 10/28/2015.
 */
public class AssetManager extends BaseSystem {

    private HashMap<String, TextureRegion> regions;

    public AssetManager(com.badlogic.gdx.assets.AssetManager assets){
        setEnabled(false);

        regions = new HashMap<String, TextureRegion>();

        Array<TextureAtlas> atlases = new Array<TextureAtlas>();
        assets.getAll(TextureAtlas.class, atlases);

        for(TextureAtlas a : atlases)
        {
            Array<TextureAtlas.AtlasRegion> regions = a.getRegions();

            for(TextureAtlas.AtlasRegion r : regions)
            {
                this.regions.put(r.name, r);
            }
        }
    }

    public TextureRegion getRegion(String name)
    {
        return regions.get(name);
    }

    @Override
    protected void processSystem() {
    }
}
