package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.audio.Sound;
import io.github.emergentorganization.cellrpg.components.SoundComponent;
import io.github.emergentorganization.cellrpg.managers.AssetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles entities' SoundEffect components.
 * Sets the SoundComponent to loop on entity insertion, then does nothing else.
 */
@Wire
public class EntitySoundSystem extends DelayedIteratingSystem {
    private final Logger logger = LogManager.getLogger(getClass());
    private AssetManager assetManager; // being a registered system, it is injected on runtime

    ComponentMapper<SoundComponent> mSound;

    public EntitySoundSystem(){
        super(Aspect.all(SoundComponent.class));
    }

    /** immediately start the sound on a loop on entity insertion */
    @Override
    protected void inserted(int entityId) {
        SoundComponent soundComp = mSound.get(entityId);

        // play the sound
        Sound soundObj = assetManager.getSoundEffect(soundComp.sound);
        logger.trace("play sound for ent #" + Integer.toString(entityId) + " : " + soundComp.sound.toString());
        float volume = 1.0f;
        soundObj.loop(volume);
    }

    /** Nothing to process, delays should never expire */
    @Override
    protected void processExpired(int e)
    {
        return;
    }

    /** Since we don't need anything, we just return a large int. */
    @Override
    protected float getRemainingDelay(int e)
    {
        return 9999999;
    }

    /**
     * Usually, this would decrease entity timer by accumulatedDelta, but in this case we aren't actually
     * timing anything, so we don't need to do anything.
     */
    @Override
    protected void processDelta(int e, float accumulatedDelta) {
        return;
    }
}
