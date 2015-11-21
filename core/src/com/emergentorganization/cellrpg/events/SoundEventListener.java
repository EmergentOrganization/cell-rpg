package com.emergentorganization.cellrpg.events;

import com.badlogic.gdx.audio.Sound;
import com.emergentorganization.cellrpg.core.SoundEffect;
import com.emergentorganization.cellrpg.managers.AssetManager;

import java.util.Map;

/**
 * Created by brian on 11/21/15.
 */
public class SoundEventListener implements EventListener {
    private Map<SoundEffect, Sound> soundEffects;

    public SoundEventListener(AssetManager assetManager) {
        soundEffects = assetManager.getSoundEffects();
    }

    @Override
    public void notify(GameEvent event) {
        switch (event) {
            case PLAYER_SHOOT:
                soundEffects.get(SoundEffect.LASER).play();
                break;
            case PLAYER_HIT:
                soundEffects.get(SoundEffect.PLAYER_HIT).play();
                break;
            case COLLISION_BULLET:
                soundEffects.get(SoundEffect.CELL_HIT).play();
                break;
        }
    }
}
