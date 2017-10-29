package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import io.github.emergentorganization.cellrpg.core.SoundEffect;

/**
 * Adds a persistent sound to an entity
 */
public class SoundComponent extends Component {
    public SoundEffect sound;
    public boolean playing = false;
}
