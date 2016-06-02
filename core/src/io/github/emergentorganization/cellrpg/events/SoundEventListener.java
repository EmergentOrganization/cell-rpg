package io.github.emergentorganization.cellrpg.events;

import com.badlogic.gdx.audio.Sound;
import io.github.emergentorganization.cellrpg.core.SoundEffect;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import io.github.emergentorganization.emergent2dcore.events.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


public class SoundEventListener implements EventListener {
    private enum Priority {
        DO_NOT_PLAY, // Ordinal number 0. As such, priority of 0 basically means it will never play in LibGDX
        LOW,
        MED,
        HIGH
    }

    private final Logger logger = LogManager.getLogger(getClass());
    private Map<SoundEffect, Sound> soundEffects;

    public SoundEventListener(AssetManager assetManager) {
        soundEffects = assetManager.getSoundEffects();
    }

    @Override
    public void notify(EntityEvent event) {
        Sound sound = null;
        Priority priority = Priority.LOW;
        switch (event.event) {
            case PLAYER_SHOOT:
                sound = soundEffects.get(SoundEffect.LASER);
                priority = Priority.HIGH;
                break;
            case PLAYER_HIT:
                sound = soundEffects.get(SoundEffect.PLAYER_HIT);
                priority = Priority.HIGH;
                break;
            case COLLISION_BULLET:
                sound = soundEffects.get(SoundEffect.CELL_HIT);
                priority = Priority.MED;
                break;
            case PLAYER_SHIELD_DOWN:
                sound = soundEffects.get(SoundEffect.LOSE);
                priority = Priority.HIGH;
                break;
            case POWERUP_PLUS:
                break;
            case POWERUP_STAR:
                sound = soundEffects.get(SoundEffect.POWERUP_PICKUP);
                priority = Priority.MED;
                break;
            case VYROID_KILL_STD:
                break;
            case VYROID_KILL_GENETIC:
                sound = soundEffects.get(SoundEffect.EXPLOSION);
                priority = Priority.LOW;
                break;
            case CA_GENERATION:
                break;
            case PLAYER_WEAPON_EMPTY:
                break;
            case SPAWN_:
                break;
        }

        if (sound != null) {
            long id = sound.play();
            sound.setPriority(id, priority.ordinal());
        }
        else {
            logger.error("Could not find sound for event " + event.event.toString());
        }
    }
}
