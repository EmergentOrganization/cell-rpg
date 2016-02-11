package io.github.emergentorganization.cellrpg.events;

import com.badlogic.gdx.audio.Sound;
import io.github.emergentorganization.cellrpg.core.SoundEffect;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.emergent2dcore.events.EventListener;

import java.util.Map;


public class SoundEventListener implements EventListener {
    private Map<SoundEffect, Sound> soundEffects;

    public SoundEventListener(AssetManager assetManager) {
        soundEffects = assetManager.getSoundEffects();
    }

    @Override
    public void notify(EntityEvent event) {
        switch (event.event) {
            case PLAYER_SHOOT:
                soundEffects.get(SoundEffect.LASER).play();
                break;
            case PLAYER_HIT:
                soundEffects.get(SoundEffect.PLAYER_HIT).play();
                break;
            case COLLISION_BULLET:
                soundEffects.get(SoundEffect.CELL_HIT).play();
                break;
            case PLAYER_SHIELD_DOWN:
                soundEffects.get(SoundEffect.LOSE).play();
                break;
            case POWERUP_PLUS:
            case POWERUP_STAR:
                soundEffects.get(SoundEffect.POWERUP_PICKUP).play();
                break;
            case VYROID_KILL_STD:
            case VYROID_KILL_GENETIC:
                soundEffects.get(SoundEffect.EXPLOSION).play();
        }
    }
}
