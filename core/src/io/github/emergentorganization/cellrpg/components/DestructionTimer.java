package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Used to track timed destruction of an entity.
 */
public class DestructionTimer extends Component {
    public long timeToDestruction = -1;  // seconds until destruction
}
