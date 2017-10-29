package io.github.emergentorganization.cellrpg.core.entityfactory.builder.componentbuilder;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import io.github.emergentorganization.cellrpg.components.SoundComponent;
import io.github.emergentorganization.cellrpg.core.SoundEffect;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Name;

/**
 * The SoundBuilder is used to build a SoundComponent.
 */
public class SoundBuilder extends BaseComponentBuilder {
    private SoundEffect sound = null;

    public SoundBuilder(SoundEffect sound) {
        super(Aspect.all(SoundComponent.class, Name.class, Bounds.class), 100);
        this.sound = sound;

    }

    @Override
    public void build(Entity entity){
        super.build(entity);

        SoundComponent soundComp = entity.getComponent(SoundComponent.class);
        if (soundComp != null) {
            String entityId = entity.getComponent(Name.class).internalID;
            if (this.sound != null) {
                soundComp.sound = this.sound;
                // Can't we add the sound & start playing on loop here? like...
                // assetManager.getSoundEffect(soundComp.sound).play();
                // Sadly, no, you can't get the assetManager from this context,
                // so this had to be done in the EntitySoundSystem.
            } else {
                throw new RuntimeException("ERROR: Need to set a sound on entity " + entityId);
            }
        }
    }

    @Override
    public Class<? extends Component> getComponentClass() {
        return SoundComponent.class;
    }
}
