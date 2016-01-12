package com.emergentorganization.cellrpg.components;

import com.artemis.Component;

/**
 * Used to track timed destruction of an entity.
 *
 * Created by 7yl4r on 1/2/2016.
 */
public class DestructionTimer extends Component {
    public long timeToDestruction = -1;  // seconds until destruction
}
