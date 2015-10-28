package com.emergentorganization.cellrpg.artemis.systems;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.emergentorganization.cellrpg.artemis.components.Position;
import com.emergentorganization.cellrpg.artemis.components.Rotation;
import com.emergentorganization.cellrpg.artemis.components.Scale;
import com.emergentorganization.cellrpg.artemis.components.Texture;

/**
 * Created by brian on 10/28/15.
 */
public class RenderSystem extends IteratingSystem {

    public RenderSystem() {
        super(Aspect.all(Position.class, Rotation.class, Scale.class, Texture.class));
    }

    @Override
    protected void process(int entityId) {

    }
}
