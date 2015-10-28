package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.artemis.components.Visual;

/**
 * Created by brian on 10/28/15.
 */
public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        super(Aspect.all(Visual.class));
    }

    @Override
    protected void process(int entityId) {
        // increment visual timestep here.
    }
}
