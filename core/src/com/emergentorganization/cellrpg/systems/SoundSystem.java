package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.emergentorganization.cellrpg.components.SFX;
import com.emergentorganization.cellrpg.core.SoundEffect;
import com.emergentorganization.cellrpg.managers.AssetManager;

import java.util.HashMap;

/**
 * Created by brian on 10/28/15.
 */
@Wire
public class SoundSystem extends IteratingSystem {
    private HashMap<SoundEffect, Sound> soundEffects;
    // WIRED
    private AssetManager assetManager;
    private ComponentMapper<SFX> sfxMapper;

    public SoundSystem() {
        super(Aspect.all(SFX.class));
    }

    @Override
    protected void initialize() {
        soundEffects = assetManager.getSoundEffects();
    }

    @Override
    protected void process(int entityId) {
        SFX sfx = sfxMapper.get(entityId);

        if (sfx.effect != SoundEffect.NONE) {
            soundEffects.get(sfx.effect).play();
            sfx.effect = SoundEffect.NONE;
        }
    }
}
