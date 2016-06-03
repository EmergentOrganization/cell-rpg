package io.github.emergentorganization.cellrpg.managers;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.emergentorganization.cellrpg.core.ParticleEff;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.core.components.Visual;
import io.github.emergentorganization.cellrpg.core.SoundEffect;
import io.github.emergentorganization.cellrpg.tools.Resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class AssetManager extends BaseSystem {

    private HashMap<String, TextureRegion> regions;
    private HashMap<String, Animation> animations;
    private Map<SoundEffect, Sound> soundEffects;
    private Map<ParticleEff, ParticleEffectPool> particlePools;
    private com.badlogic.gdx.assets.AssetManager gdxAssetManager;

    public AssetManager(com.badlogic.gdx.assets.AssetManager assets) {
        setEnabled(false);
        gdxAssetManager = assets;

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

        HashMap<SoundEffect, Sound> effects = new HashMap<SoundEffect, Sound>();
        for (Map.Entry<SoundEffect, String> effectPathSet : Resources.SFX_FILENAME_MAP.entrySet()) {
            effects.put(effectPathSet.getKey(), assets.get(effectPathSet.getValue(), Sound.class));
        }
        soundEffects = Collections.unmodifiableMap(effects);

        HashMap<ParticleEff, ParticleEffectPool> pEffects = new HashMap<ParticleEff, ParticleEffectPool>();
        for (Map.Entry<ParticleEff, String> effectPathSet : Resources.PFX_FILENAME_MAP.entrySet()) {
            addParticleEffectTo(effectPathSet, pEffects);
        }
        particlePools = Collections.unmodifiableMap(pEffects);
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

    public ParticleEffect getParticleEffect(ParticleEff effKey){
        return particlePools.get(effKey).obtain();
    }

    public Map<SoundEffect, Sound> getSoundEffects() {
        return soundEffects;
    }

    @Override
    protected void processSystem() {
    }

    private static void addParticleEffectTo(Map.Entry<ParticleEff, String> effectPathSet,
                                            HashMap<ParticleEff, ParticleEffectPool> pEffects){
        ParticleEffect prototype = new ParticleEffect();
        prototype.load(Gdx.files.internal(effectPathSet.getValue()), Gdx.files.internal(Resources.DIR_PARTICLES));
        prototype.scaleEffect(EntityFactory.SCALE_WORLD_TO_BOX / 2f);
        pEffects.put(effectPathSet.getKey(), new ParticleEffectPool(prototype, 0, 50));
    }
}
