package com.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.emergentorganization.cellrpg.components.Visual;
import com.emergentorganization.cellrpg.core.SoundEffect;
import com.emergentorganization.cellrpg.tools.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by orelb on 10/28/2015.
 */
public class AssetManager extends BaseSystem {

    private HashMap<String, TextureRegion> regions;
    private HashMap<String, Animation> animations;
    private HashMap<SoundEffect, Sound> soundEffects;

    public AssetManager(com.badlogic.gdx.assets.AssetManager assets) {
        setEnabled(false);

        regions = new HashMap<String, TextureRegion>();
        animations = new HashMap<String, Animation>();

        Array<TextureAtlas> atlases = new Array<TextureAtlas>();
        assets.getAll(TextureAtlas.class, atlases);

        for (TextureAtlas a : atlases) {
            Array<TextureAtlas.AtlasRegion> regions = a.getRegions();

            for (TextureAtlas.AtlasRegion r : regions) {
                this.regions.put(r.name, r);
            }
        }

        soundEffects = new HashMap<SoundEffect, Sound>();
        for (Map.Entry<SoundEffect, String> effectPathSet : Resources.SFX_FILENAME_MAP.entrySet()) {
            soundEffects.put(effectPathSet.getKey(), assets.get(effectPathSet.getValue(), Sound.class));
        }
    }

    public Animation defineAnimation(String id, float frameDuration, String[] frames, Animation.PlayMode playMode) {
        Animation a = animations.get(id);
        if (a != null)
            return a;

        Array<TextureRegion> frameList = new Array<TextureRegion>();
        for (String frame : frames) {
            frameList.add(getRegion(frame));
            regions.remove(frame); // better remove these frames from the regions map, they are not going to be retrieved directly anyway.
        }

        a = new Animation(frameDuration, frameList, playMode);
        animations.put(id, a);
        return a;
    }

    public TextureRegion getRegion(String id) {
        return regions.get(id);
    }

    public Animation getAnimation(String id) {
        return animations.get(id);
    }

    /**
     * @param v
     * @return current frame in animation, or the only frame if a static region
     */
    public TextureRegion getCurrentRegion(Visual v) {
        TextureRegion r = null;

        if (v.isAnimation) {
            Animation a = getAnimation(v.id);
            if (a != null) {
                r = a.getKeyFrame(v.stateTime);
            }
        } else {
            r = getRegion(v.id);
        }

        return r;
    }

    public Sound getSoundEffect(SoundEffect effect) {
        return soundEffects.get(effect);
    }

    public HashMap<SoundEffect, Sound> getSoundEffects() {
        return soundEffects;
    }

    @Override
    protected void processSystem() {
    }
}
