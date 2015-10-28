package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.artemis.components.Animation;

/**
 * Created by brian on 10/28/15.
 */
public class AnimationSystem extends IteratingSystem {

    public AnimationSystem() {
        super(Aspect.all(Animation.class));
    }

    @Override
    protected void process(int entityId) {

    }
}
