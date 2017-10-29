package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.audio.Sound;
import io.github.emergentorganization.cellrpg.components.SoundComponent;
import io.github.emergentorganization.cellrpg.managers.AssetManager;

/**
 * Handles entitys' SoundEffect components
 */
@Wire
public class EntitySoundSystem extends DelayedIteratingSystem {
    private AssetManager assetManager; // being a registered system, it is injected on runtime

    ComponentMapper<SoundComponent> mSound;

    public EntitySoundSystem(){
        super(Aspect.all(SoundComponent.class));
    }

    /** Process entity when corresponding timer <= 0. */
    @Override
    protected void processExpired(int e)
    {
        SoundComponent soundComp = mSound.get(e);

        // play the sound
        Sound soundObj = assetManager.getSoundEffect(soundComp.sound);
        float volume = 1.0f;
//        soundObj.play(volume);
        soundObj.loop(volume);

        // Provide new cooldown when done.
        soundComp.playing = true;
    }

    /** Returns a large delay if the sound is playing, else returns <=0 . This is to signal no action needed
     * if already playing, and immediate action needed if sound is not playing. */
    @Override
    protected float getRemainingDelay(int e)
    {
        SoundComponent soundComp = mSound.get(e);
        if (soundComp.playing){
            return 10000;
        } else {
            return -1;
        }
    }

    /**
     * Usually, this would decrease entity timer by accumulatedDelta, but in this case we aren't actually
     * timing anything, so we don't need to do anything.
     */
    @Override
    protected void processDelta(int e, float accumulatedDelta)
    {
        return;
    }
}
